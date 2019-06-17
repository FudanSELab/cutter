package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.po.Sql;
import cn.icedsoul.cutter.domain.po.Table;
import cn.icedsoul.cutter.relation.CloseTo;
import cn.icedsoul.cutter.repository.*;
import cn.icedsoul.cutter.service.api.WeightCalculationService;
import com.google.common.collect.Lists;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static cn.icedsoul.cutter.util.Common.*;

@Service
@Log
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
        addSameTraceWeight();
        addSameScenarioWeight();
//        addSameModuleWeight();
        System.out.println("!!!Finish adding weight!!!");
    }

    @Override
    public List<Double[][]> addSimilarWeight() {
        List<Table> tables = Lists.newArrayList(tableRepository.findAll());
        List<Double[][]> graph = new ArrayList<>();
        Double[][] sqlWeightGraph = new Double[tables.size()][tables.size()];
        Double[][] traceWeightGraph = new Double[tables.size()][tables.size()];
        Double[][] scenarioWeightGraph = new Double[tables.size()][tables.size()];

        for (int i = 0; i < tables.size(); i++){
            sqlWeightGraph[i][i] = 1.0;
            traceWeightGraph[i][i] = 1.0;
            scenarioWeightGraph[i][i] = 1.0;
            for (int j = i + 1; j < tables.size(); j++){
                List<Double> result = calculateSimilar(tables.get(i), tables.get(j));
                sqlWeightGraph[j][i] = sqlWeightGraph[i][j] = result.get(0);
                traceWeightGraph[j][i] = traceWeightGraph[i][j] = result.get(1);
                scenarioWeightGraph[j][i] = scenarioWeightGraph[i][j] = result.get(2);
            }
        }
        graph.add(sqlWeightGraph);
        graph.add(traceWeightGraph);
        graph.add(scenarioWeightGraph);
        return graph;

    }

    private List<Double> calculateSimilar(Table a, Table b){
        Double aSqlNum = 0.0, bSqlNum = 0.0, abSqlNum = 0.0;
        for(Long aSqlId : a.getAppearSql()){
            for(Long bSqlId : b.getAppearSql()){
                if(aSqlId.equals(bSqlId)){
                    abSqlNum += sqlWeight.get(aSqlId);
                }
                if(aSqlNum == 0) {
                    bSqlNum += sqlWeight.get(bSqlId);
                }
            }
            aSqlNum += sqlWeight.get(aSqlId);
        }

        Double aTraceNum = 0.0, bTraceNum = 0.0, abTraceNum = 0.0;
        for(Long aTraceId : a.getAppearTrace()){
            for(Long bTraceId : b.getAppearTrace()){
                if(aTraceId.equals(bTraceId)){
                    abTraceNum += traceWeight.get(aTraceId);
                }
                if(aTraceNum == 0){
                    bTraceNum += traceWeight.get(bTraceId);
                }
            }
            aTraceNum += traceWeight.get(aTraceId);
        }
        Double aScenarioNum = 0.0, bScenarioNum = 0.0, abScenarioNum = 0.0;
        for(String aScenarioId : a.getAppearScenario()){
            for(String bScenarioId : b.getAppearScenario()){
                if(aScenarioId.equals(bScenarioId)){
                    abScenarioNum += scenarioWeight.get(aScenarioId);
                }
                if(aScenarioNum == 0){
                    bScenarioNum += scenarioWeight.get(bScenarioId);
                }
            }
            aScenarioNum += scenarioWeight.get(aScenarioId);
        }
        Double sqlSimilar = abSqlNum / (aSqlNum + bSqlNum - abSqlNum);
        Double traceSimilar = abTraceNum / (aTraceNum + bTraceNum - abTraceNum);
        Double scenarioSimilar = abScenarioNum / (aScenarioNum + bScenarioNum - abScenarioNum);
        List<Double> result = new ArrayList<>();
//        log.info(String.format("%f %f %f %f %f %f %f %f %f", aSqlNum, bSqlNum, abSqlNum, aTraceNum, bTraceNum, abTraceNum, aScenarioNum, bScenarioNum, abScenarioNum));
        log.info(String.format("%s %s : %.2f %.2f %.2f", a.getTableName(), b.getTableName(), sqlSimilar, traceSimilar, scenarioSimilar));
        result.add(sqlSimilar);
        result.add(traceSimilar);
        result.add(scenarioSimilar);
        return result;
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
            Double sqlFrequency = sqlRepository.getSumSqlFrequencyBySqlId(sql.getId());
//            System.out.println("---sqlId="+sql.getId() + " sqlFrequency=" + sqlFrequency);
            if(sqlFrequency > 0){
                //查询这条sql操作的所有table，两两之间连条边
                List<Table> tables = tableRepository.findTablesBySql(sql.getDatabaseName(), sql.getSql());
                checkAndSetWeight2(tables, SQL_LEVEL, sqlFrequency);
            }
        }
        System.out.println("Finish adding same sql weight");
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
                    Double traceFrequency = tl.get(0);
//                    System.out.println("---traceId="+traceId +" ---traceFrequency=" + traceFrequency);
                    if(traceFrequency > 0){
                        //获取一条trace中的所有table，两两之间连条边
                        List<Table> tables = tableRepository.findTablesOfSameTrace(traceId);
                        checkAndSetWeight2(tables, TRACE_LEVEL, traceFrequency);
                    }
                }
            }
        }
        System.out.println("Finish adding same trace weight");
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
                    Double scenarioFrequency = fl.get(0);
//                    System.out.println("======scenarioId="+scenarioId + "==scenarioFrequency=" + scenarioFrequency);
                    if(scenarioFrequency > 0){
                        //获取一个场景中涉及到的所有table，两两之间连条边
                        List<Table> tables = tableRepository.findTablesOfSameScenario(scenarioId);
                        checkAndSetWeight2(tables, SCENARIO_LEVEL, scenarioFrequency);
                    }
                }
            }
        }
        System.out.println("Finish adding same scenario weight");
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
                    Double moduleFrequency = methodCallRepository.getFrequencyByModuleName(module);
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
        checkAndSetWeight(tables, PACKAGE_LEVEL, 0.0);
    }

    //每级都连边
    private void checkAndSetWeight2(List<Table> tables, int level, Double frequency){
        if(tables != null){
            for(int i = 0; i < tables.size(); i++) {
                for (int j = i + 1; j < tables.size(); j++) {
                    List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTableAndLevel(
                            tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                            tables.get(j).getDatabaseName(), tables.get(j).getTableName(), level);
                    if(null == closeToList || closeToList.size() == 0){
                        CloseTo closeTo = new CloseTo();
                        closeTo.setStartTable(tables.get(i));
                        closeTo.setEndTable(tables.get(j));
                        closeTo.setLevel(level);
                        closeTo.setWeight(getUpdatedWeight(0.0, frequency, level));
                        closeToRepository.save(closeTo);
                    } else if(closeToList.size() > 1){
                        System.out.println("!!!!!!Error: Two tables has more than one edge at one level!!!!!!!");
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

    //前几级连过的边不再连
    private void checkAndSetWeight(List<Table> tables, int level, Double frequency){
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
                            closeTo.setWeight(getUpdatedWeight(0.0, frequency, level));
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


    private Double getUpdatedWeight(Double weight, Double frequency, int level){
        switch(level){
            case SQL_LEVEL:{
                return weight + 0.02*frequency + 25;
            }
            case TRACE_LEVEL:{
                return weight + 0.015*frequency + 15;
            }
            case SCENARIO_LEVEL:{
                return weight + 0.01*frequency + 10;
            }
            case PACKAGE_LEVEL:{
                return weight + 5;
            }
            case MODULE_LEVEL:{
                return weight + 0.1 * frequency + 5;
            }
            default: return -1.0;
        }

//        switch(level){
//            case SQL_LEVEL:{
//                return weight + 10*frequency + 100;
//            }
//            case TRACE_LEVEL:{
//                return weight + 5*frequency + 50;
//            }
//            case SCENARIO_LEVEL:{
//                return weight + 2*frequency + 10;
//            }
//            case PACKAGE_LEVEL:{
//                return weight + 5;
//            }
//            case MODULE_LEVEL:{
//                return weight + 1 * frequency + 5;
//            }
//            default: return -1;
//        }

//        switch(level){
//            case SQL_LEVEL:{
//                return weight + 10;
//            }
//            case TRACE_LEVEL:{
//                return weight + 50;
//            }
//            case SCENARIO_LEVEL:{
//                return weight + 100;
//            }
//            case PACKAGE_LEVEL:{
//                return weight + 5;
//            }
//            case MODULE_LEVEL:{
//                return weight + 5;
//            }
//            default: return -1;
//        }
    }



}
