package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.relation.CloseTo;
import cn.icedsoul.cutter.repository.*;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void addWeight() {
        //clean up
        closeToRepository.deleteAll();

//        addSameSqlWeight();
//        addSameTraceWeight();
//        addSameScenarioWeight();
//        addSameModuleWeight();
    }


    public void addSameScenarioWeight(){
        //获取所有场景
        List<String> scenarioList = methodCallRepository.listAllScenario();
        if(scenarioList != null){
            for(String scenarioId:scenarioList){
                if(null != scenarioId && !"<no-scenario-id>".equals(scenarioId)){
                    //获取场景的执行频率
                    List<Double> fl = methodCallRepository.getTraceFrequencyByScenarioId(scenarioId);
                    if(fl == null || fl.size() != 1){
                        System.out.println("!!!Error! one trace has different scenarioFrequency!!!");
                        break;
                    }
                    double scenarioFrequency = fl.get(0);
                    System.out.println("======scenarioId="+scenarioId + "==scenarioFrequency=" + scenarioFrequency);
                    if(scenarioFrequency > 0){
                        //获取一个场景中涉及到的所有table，两两之间连条边
                        List<Table> tables = tableRepository.findTablesOfSameScenario(scenarioId);
                        for(int i = 0; i < tables.size(); i++) {
                            for (int j = i + 1; j < tables.size(); j++) {
                                boolean hasEdgeBefore = closeToRepository.findCloseToBetweenTwoTablesAndLevelLessThan(
                                        tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                        tables.get(j).getDatabaseName(), tables.get(j).getTableName(),3);
                                if( ! hasEdgeBefore ){
                                    List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTableAndLevel(
                                            tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                            tables.get(j).getDatabaseName(), tables.get(j).getTableName(),3);
                                    if(null == closeToList || closeToList.size() == 0){
                                        CloseTo closeTo = new CloseTo();
                                        closeTo.setStartTable(tables.get(i));
                                        closeTo.setEndTable(tables.get(j));
                                        closeTo.setLevel(3);
                                        closeTo.setWeight(addWeightBySameScenario(0, scenarioFrequency));
                                        closeToRepository.save(closeTo);
                                    } else if(closeToList.size() > 1){
                                        System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                                    } else {
                                        double d = closeToRepository.setWeight(
                                                tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                                tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
                                                3, addWeightBySameSql(closeToList.get(0), scenarioFrequency));
                                        System.out.println("final weight=" + d);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void addSameTraceWeight(){
        //获取所有trace
        List<Long> traceList = methodCallRepository.listAllTrace();
        if(traceList != null){
            for(Long traceId: traceList){
                if(traceId > 0){
                    //获取一条trace对应的scenarioFrequency
                    List<Double> tl = methodCallRepository.getTraceFrequencyByTraceId(traceId);
                    if(null == tl || tl.size() != 1){
                        System.out.println("!!!Error! one scenario has different scenarioFrequency!!!");
                        break;
                    }
                    double traceFrequency = tl.get(0);
                    System.out.println("---traceId="+traceId +" ---traceFrequency=" + traceFrequency);
                    if(traceFrequency > 0){
                        //获取一条trace中的所有table，两两之间连条边
                        List<Table> tables = tableRepository.findTablesOfSameTrace(traceId);
                        for(int i = 0; i < tables.size(); i++){
                            for(int j = i +1; j < tables.size(); j++){
                                boolean hasEdgeBefore = closeToRepository.findCloseToBetweenTwoTablesAndLevelLessThan(
                                        tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                        tables.get(j).getDatabaseName(), tables.get(j).getTableName(),2);
                                if( ! hasEdgeBefore ){
                                    List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTableAndLevel(
                                            tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                            tables.get(j).getDatabaseName(), tables.get(j).getTableName(), 2);
                                    if(null == closeToList || closeToList.size() == 0){
                                        CloseTo closeTo = new CloseTo();
                                        closeTo.setStartTable(tables.get(i));
                                        closeTo.setEndTable(tables.get(j));
                                        closeTo.setLevel(2);
                                        closeTo.setWeight(addWeightBySameTrace(0, traceFrequency));
                                        closeToRepository.save(closeTo);
                                    } else if(closeToList.size() > 1){
                                        System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                                    } else {
                                        double d = closeToRepository.setWeight(
                                                tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                                tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
                                                2, addWeightBySameTrace(closeToList.get(0), traceFrequency));
//                                        System.out.println("final weight=" + d);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

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
                if(tables != null && tables.size() > 1){
                    for(int i = 0; i < tables.size(); i++){
                        for(int j = i + 1; j < tables.size(); j++){
                            List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTableAndLevel(
                                    tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                    tables.get(j).getDatabaseName(), tables.get(j).getTableName(),1);
                            System.out.println("-----"+closeToList);
                            if(null == closeToList || closeToList.size() == 0){
                                CloseTo closeTo = new CloseTo();
                                closeTo.setStartTable(tables.get(i));
                                closeTo.setEndTable(tables.get(j));
                                closeTo.setLevel(1);
                                closeTo.setWeight(addWeightBySameSql(0, sqlFrequency));
                                closeToRepository.save(closeTo);
                            } else if(closeToList.size() > 1){
                                System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                            } else {
                                double d = closeToRepository.setWeight(
                                        tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                        tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
                                        1,  addWeightBySameSql(closeToList.get(0), sqlFrequency));
//                                System.out.println("final weight=" + d);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addSameModuleWeight() {
        //获取所有模块名
        List<String> moduleList = methodCallRepository.listAllModule();
        if(moduleList != null && moduleList.size() > 0){
            for(String module: moduleList){
                if( module != null  && !"no-module-name".equals(module)){
                    //获取module下所有trace的调用频率总和
                    double moduleFrequency = methodCallRepository.getModuleFrequencyByModuleName(module);
                    System.out.println("--moduleFrequency=" + moduleFrequency);
                    //获取同一个module下的所有table
                    List<Table> tables =  tableRepository.findTablesOfSameModule(module);
                    System.out.println(tables);
                    //table之间两两连条边
                    for(int i = 0; i < tables.size(); i++) {
                        for (int j = i + 1; j < tables.size(); j++) {
                            boolean hasEdgeBefore = closeToRepository.findCloseToBetweenTwoTablesAndLevelLessThan(
                                    tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                    tables.get(j).getDatabaseName(), tables.get(j).getTableName(),5);
                            if( ! hasEdgeBefore ){
                                List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTableAndLevel(
                                        tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                        tables.get(j).getDatabaseName(), tables.get(j).getTableName(),5);
                                if(null == closeToList || closeToList.size() == 0){
                                    CloseTo closeTo = new CloseTo();
                                    closeTo.setStartTable(tables.get(i));
                                    closeTo.setEndTable(tables.get(j));
                                    closeTo.setLevel(5);
                                    closeTo.setWeight(addWeightBySameModule(0, moduleFrequency));
                                    closeToRepository.save(closeTo);
                                } else if(closeToList.size() > 1){
                                    System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                                } else {
                                    double d = closeToRepository.setWeight(
                                            tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                            tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
                                            5, addWeightBySameModule(closeToList.get(0), moduleFrequency));
//                                    System.out.println("final weight=" + d);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public void addSamePackageWeight() {
        //TODO 目前需要手动输入要遍历的包名，需要改成一个函数
        long packageId = 564;

        //获取一个包中涉及到的所有table
        List<Table> tables = tableRepository.findTablesOfSamePackage(packageId);
        System.out.println(tables);
        //table之间两两连条边
        for(int i = 0; i < tables.size(); i++) {
            for (int j = i + 1; j < tables.size(); j++) {
                boolean hasEdgeBefore = closeToRepository.findCloseToBetweenTwoTablesAndLevelLessThan(
                        tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                        tables.get(j).getDatabaseName(), tables.get(j).getTableName(),4);
                if( ! hasEdgeBefore ){
                    List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTableAndLevel(
                            tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                            tables.get(j).getDatabaseName(), tables.get(j).getTableName(),4);
                    if(null == closeToList || closeToList.size() == 0){
                        CloseTo closeTo = new CloseTo();
                        closeTo.setStartTable(tables.get(i));
                        closeTo.setEndTable(tables.get(j));
                        closeTo.setLevel(4);
                        closeTo.setWeight(addWeightBySamePackage(0));
                        closeToRepository.save(closeTo);
                    } else if(closeToList.size() > 1){
                        System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                    } else {
                        double d = closeToRepository.setWeight(
                                tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
                                4, addWeightBySamePackage(closeToList.get(0)));
                        System.out.println("final weight=" + d);
                    }
                }
            }
        }
    }



    private double addWeightBySameSql(double weight, double sqlFrequency){
        //TODO 根据频率增加weight
        return weight + 10*sqlFrequency + 100;
    }

    private double addWeightBySameTrace(double weight, double traceFrequency){
        //TODO 根据频率增加weight
        return weight + 5*traceFrequency + 50;
    }

    private double addWeightBySameScenario(double weight, double scenarioFrequency){
        //TODO 根据频率增加weight
        return weight + 2*scenarioFrequency + 10;
    }

    private double addWeightBySameModule(double weight, double moduleFrequency){
        //TODO 根据频率增加weight
        return weight + 0.1 * moduleFrequency + 5;
    }

    private double addWeightBySamePackage(double weight){
        //TODO 根据频率增加weight
        return weight + 5;
    }
}
