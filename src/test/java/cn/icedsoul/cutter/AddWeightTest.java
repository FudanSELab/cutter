package cn.icedsoul.cutter;

import cn.icedsoul.cutter.domain.Method;
import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.relation.CloseTo;
import cn.icedsoul.cutter.relation.Contain;
import cn.icedsoul.cutter.relation.MethodCall;
import cn.icedsoul.cutter.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AddWeightTest {

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

    @Test
    public void testMethod() {
        //clean up
        closeToRepository.deleteAll();

        addSameSqlWeight();
        addSameTraceWeight();
        addSameScenarioWeight();
    }

    public void addSameScenarioWeight(){
        List<String> scenarioList = methodCallRepository.listAllScenario();
        if(scenarioList != null){
            for(String scenarioId:scenarioList){
                if(null != scenarioId && !"<no-scenario-id>".equals(scenarioId)){
                    List<Double> fl = methodCallRepository.getTraceFrequencyByScenarioId(scenarioId);
                    if(fl == null || fl.size() != 1){
                        System.out.println("!!!Error! one trace has different scenarioFrequency!!!");
                        break;
                    }
                    double scenarioFrequency = fl.get(0);
                    System.out.println("======scenarioId="+scenarioId + "==scenarioFrequency=" + scenarioFrequency);
                    if(scenarioFrequency > 0){
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

    public void addSameSqlWeight(){
        Iterator<Sql> sqlIterator = sqlRepository.findAll().iterator();
        while(sqlIterator.hasNext()){
            Sql sql = sqlIterator.next();
            System.out.println("---sql:"+sql.toString());
            double sqlFrequency = sqlRepository.getSumSqlFrequencyBySqlId(sql.getId());
            System.out.println("---sqlId="+sql.getId() + " sqlFrequency=" + sqlFrequency);
            if(sqlFrequency > 0){
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
                                System.out.println("final weight=" + d);
                            }
                        }
                    }
                }
            }
        }
    }

    public double addWeightBySameSql(double weight, double sqlFrequency){
        //TODO 根据频率增加weight
        return weight + 10*sqlFrequency + 100;
    }

    public double addWeightBySameTrace(double weight, double traceFrequency){
        //TODO 根据频率增加weight
        return weight + 5*traceFrequency + 50;
    }

    public double addWeightBySameScenario(double weight, double scenarioFrequency){
        //TODO 根据频率增加weight
        return weight + 2*scenarioFrequency + 10;
    }
}
