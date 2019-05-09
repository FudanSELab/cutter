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

//        List<Method> l = methodRepository.findEntry();
//        if(l != null && l.size() == 1){
//            Method entry = l.get(0);
//            System.out.println("entryId="+entry.getId());
//
//
//        } else {
//            System.out.println("Error: Entry node is not exist or is not unique!");
//        }

    }

    public void addSameScenarioWeight(){
        List<String> scenarioList = methodCallRepository.listAllScenario();
        if(scenarioList != null){
            for(String scenarioId:scenarioList){
                if(null != scenarioId && !"<no-scenario-id>".equals(scenarioId)){
                    System.out.println("======scenarioId="+scenarioId);
                    List<Table> tables = tableRepository.findTablesOfSameScenario(scenarioId);
                    for(int i = 0; i < tables.size(); i++) {
                        for (int j = i + 1; j < tables.size(); j++) {
                            List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTable(
                                    tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                    tables.get(j).getDatabaseName(), tables.get(j).getTableName());
                            if(null == closeToList || closeToList.size() == 0){
                                CloseTo closeTo = new CloseTo();
                                closeTo.setStartTable(tables.get(i));
                                closeTo.setEndTable(tables.get(j));
                                closeTo.setWeight(addWeightBySameScenario(0));
                                closeToRepository.save(closeTo);
                            } else if(closeToList.size() > 1){
                                System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                            } else {
//                            double d = closeToRepository.setWeight(
//                                    tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
//                                    tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
//                                    addWeightBySameSql(closeToList.get(0)));
//                            System.out.println("final weight=" + d);
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
                    System.out.println("---traceId="+traceId);
                    List<Table> tables = tableRepository.findTablesOfSameTrace(traceId);
                    for(int i = 0; i < tables.size(); i++){
                        for(int j = i +1; j < tables.size(); j++){
                            List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTable(
                                    tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                    tables.get(j).getDatabaseName(), tables.get(j).getTableName());
                            if(null == closeToList || closeToList.size() == 0){
                                CloseTo closeTo = new CloseTo();
                                closeTo.setStartTable(tables.get(i));
                                closeTo.setEndTable(tables.get(j));
                                closeTo.setWeight(addWeightBySameTrace(0));
                                closeToRepository.save(closeTo);
                            } else if(closeToList.size() > 1){
                                System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                            } else {
//                            double d = closeToRepository.setWeight(
//                                    tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
//                                    tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
//                                    addWeightBySameSql(closeToList.get(0)));
//                            System.out.println("final weight=" + d);
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
            System.out.println(sql.toString());
            List<Table> tables = tableRepository.findTablesBySql(sql.getDatabaseName(), sql.getSql());
            System.out.println(tables.toString());
            if(tables != null && tables.size() > 1){
                for(int i = 0; i < tables.size(); i++){
                    for(int j = i + 1; j < tables.size(); j++){
                        List<Double> closeToList = closeToRepository.findCloseToByStartTableAndEndTable(
                                tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                tables.get(j).getDatabaseName(), tables.get(j).getTableName());
                        System.out.println("-----"+closeToList);
                        if(null == closeToList || closeToList.size() == 0){
                            CloseTo closeTo = new CloseTo();
                            closeTo.setStartTable(tables.get(i));
                            closeTo.setEndTable(tables.get(j));
                            closeTo.setWeight(addWeightBySameSql(0));
                            closeToRepository.save(closeTo);
                        } else if(closeToList.size() > 1){
                            System.out.println("!!!!!!Error: Two tables has more than one edge!!!!!!!");
                        } else {
                            double d = closeToRepository.setWeight(
                                    tables.get(i).getDatabaseName(), tables.get(i).getTableName(),
                                    tables.get(j).getDatabaseName(), tables.get(j).getTableName(),
                                    addWeightBySameSql(closeToList.get(0)));
                            System.out.println("final weight=" + d);
                        }
                    }
                }
            }
        }
    }

    public double addWeightBySameSql(double weight){
        //TODO 根据频率增加weight
        return weight + 100;
    }

    public double addWeightBySameTrace(double weight){
        //TODO 根据频率增加weight
        return weight + 50;
    }

    public double addWeightBySameScenario(double weight){
        //TODO 根据频率增加weight
        return weight + 10;
    }
}
