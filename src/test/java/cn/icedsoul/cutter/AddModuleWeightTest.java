package cn.icedsoul.cutter;

import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.relation.CloseTo;
import cn.icedsoul.cutter.repository.CloseToRepository;
import cn.icedsoul.cutter.repository.MethodCallRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddModuleWeightTest {

    @Autowired
    MethodCallRepository methodCallRepository;
    @Autowired
    TableRepository tableRepository;
    @Autowired
    CloseToRepository closeToRepository;

    @Test
    public void testMethod() {
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

    public double addWeightBySameModule(double weight, double moduleFrequency){
        //TODO 根据频率增加weight
        return weight + 0.1 * moduleFrequency + 5;
    }
}
