package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.relation.CloseTo;
import cn.icedsoul.cutter.repository.*;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
public class WeightCalculationServiceImpl implements WeightCalculationService {

    @Autowired
    MethodRepository methodRepository;
    @Autowired
    SqlRepository sqlRepository;
    @Autowired
    TableRepository tableRepository;
    @Autowired
    ContainRepository containRepository;
    @Autowired
    ExecuteRepository executeRepository;
    @Autowired
    MethodCallRepository methodCallRepository;
    @Autowired
    CloseToRepository closeToRepository;

    private final static int SQL_LEVEL = 1;
    private final static int TRACE_LEVEL = 2;
    private final static int SCENARIO_LEVEL = 3;
    private final static int PACKAGE_LEVEL = 4;
    private final static int MODULE_LEVEL = 5;


    @Override
    public void addWeight() {
        //clean up
        closeToRepository.deleteAll();

        addSameSqlWeight();
//        addSameTraceWeight();
//        addSameScenarioWeight();
//        addSameModuleWeight();
        System.out.println("!!!Finish adding weight!!!");
    }

    @Override
    @Transactional
    public void addSameSqlWeight(){
        //查询所有的sql语句
        Iterator<Sql> sqlIterator = sqlRepository.findAll().iterator();
        while(sqlIterator.hasNext()){
            Sql sql = sqlIterator.next();
//            System.out.println("---sql:"+sql.toString());
            //获取所有调用这条sql的方法的总的调用频率
            double sqlFrequency = sqlRepository.getSumSqlFrequencyBySqlId(sql.getId());
            System.out.println("---sqlId="+sql.getId() + " sqlFrequency=" + sqlFrequency);
            if(sqlFrequency > 0){
                //查询这条sql操作的所有table，两两之间连条边
                List<Table> tables = tableRepository.findTablesBySql(sql.getDatabaseName(), sql.getSql());
                System.out.println(tables.toString());
                checkAndSetWeight(tables, SQL_LEVEL, sqlFrequency);
            }
        }
    }

    @Override
    @Transactional
    public void addSameTraceWeight(){
        //获取所有trace
        List<Long> traceList = methodCallRepository.listAllTrace();
        if(traceList != null){
            for(Long traceId: traceList){
                if(traceId > 0){
                    //获取一条trace对应的scenarioFrequency
                    List<Double> tl = methodCallRepository.getFrequencyByTraceId(traceId);
                    if(null == tl || tl.size() != 1){
                        System.out.println("!!!Error! one scenario has different scenarioFrequency!!!");
                        break;
                    }
                    double traceFrequency = tl.get(0);
                    System.out.println("---traceId="+traceId +" ---traceFrequency=" + traceFrequency);
                    if(traceFrequency > 0){
                        //获取一条trace中的所有table，两两之间连条边
                        List<Table> tables = tableRepository.findTablesOfSameTrace(traceId);
                        checkAndSetWeight(tables, TRACE_LEVEL, traceFrequency);
                    }
                }
            }
        }
    }



    @Override
    @Transactional
    public void addSameScenarioWeight(){
        //获取所有场景
        List<String> scenarioList = methodCallRepository.listAllScenario();
        if(scenarioList != null){
            for(String scenarioId:scenarioList){
                if(null != scenarioId && !"<no-scenario-id>".equals(scenarioId)){
                    //获取场景的执行频率
                    List<Double> fl = methodCallRepository.getFrequencyByScenarioId(scenarioId);
                    if(fl == null || fl.size() != 1){
                        System.out.println("!!!Error! one trace has different scenarioFrequency!!!");
                        break;
                    }
                    double scenarioFrequency = fl.get(0);
                    System.out.println("======scenarioId="+scenarioId + "==scenarioFrequency=" + scenarioFrequency);
                    if(scenarioFrequency > 0){
                        //获取一个场景中涉及到的所有table，两两之间连条边
                        List<Table> tables = tableRepository.findTablesOfSameScenario(scenarioId);
                        checkAndSetWeight(tables, SCENARIO_LEVEL, scenarioFrequency);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void addSameModuleWeight() {
        //获取所有模块名
        List<String> moduleList = methodCallRepository.listAllModule();
        if(moduleList != null && moduleList.size() > 0){
            for(String module: moduleList){
                if( module != null  && !"no-module-name".equals(module)){
                    //获取module下所有trace的调用频率总和
                    double moduleFrequency = methodCallRepository.getFrequencyByModuleName(module);
                    System.out.println("--moduleFrequency=" + moduleFrequency);
                    //获取同一个module下的所有table
                    List<Table> tables =  tableRepository.findTablesOfSameModule(module);
//                    System.out.println(tables);
                    //table之间两两连条边
                    checkAndSetWeight(tables, MODULE_LEVEL, moduleFrequency);
                }
            }
        }
    }

    @Override
    @Transactional
    public void addSamePackageWeight() {
        //TODO 目前需要手动输入要遍历的包名，需要改成一个函数
        long packageId = 564;

        //获取一个包中涉及到的所有table
        List<Table> tables = tableRepository.findTablesOfSamePackage(packageId);
        System.out.println(tables);
        //table之间两两连条边
        checkAndSetWeight(tables, PACKAGE_LEVEL, 0);
    }


    private void checkAndSetWeight(List<Table> tables, int level, double frequency){
        if(tables != null){
            for(int i = 0; i < tables.size(); i++){
                for(int j = i +1; j < tables.size(); j++){
                    boolean hasEdgeBefore;
                    if(level == SQL_LEVEL){
                        hasEdgeBefore = false;
                    }else {
                        hasEdgeBefore = closeToRepository.findCloseToBetweenTwoTablesAndLevelLessThan(
                                tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                tables.get(j).getDatabaseName(), tables.get(j).getTableName(), level);
                    }
                    if( ! hasEdgeBefore ){
                        List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTableAndLevel(
                                tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                tables.get(j).getDatabaseName(), tables.get(j).getTableName(), level);
                        if(null == closeToList || closeToList.size() == 0){
                            CloseTo closeTo = new CloseTo();
                            closeTo.setStartTable(tables.get(i));
                            closeTo.setEndTable(tables.get(j));
                            closeTo.setLevel(level);
                            closeTo.setWeight(getUpdatedWeight(0, frequency, level));
                            closeToRepository.save(closeTo);
                        } else if(closeToList.size() > 1){
                            System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                        } else {
                            closeToRepository.setWeight(
                                    tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                    tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
                                    level, getUpdatedWeight(closeToList.get(0), frequency, level));
                        }
                    }
                }
            }
        }
    }

    private double getUpdatedWeight(double weight, double frequency, int level){
        switch(level){
            case SQL_LEVEL:{
                return weight + 10*frequency + 100;
            }
            case TRACE_LEVEL:{
                return weight + 5*frequency + 50;
            }
            case SCENARIO_LEVEL:{
                return weight + 2*frequency + 10;
            }
            case PACKAGE_LEVEL:{
                return weight + 5;
            }
            case MODULE_LEVEL:{
                return weight + 0.1 * frequency + 5;
            }
            default: return -1;
        }
    }

}
