package cn.icedsoul.cutter;

import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.relation.CloseTo;
import cn.icedsoul.cutter.repository.CloseToRepository;
import cn.icedsoul.cutter.repository.PackageRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddPackageWeightTest {

    @Autowired
    CloseToRepository closeToRepository;
    @Autowired
    PackageRepository packageRepository;
    @Autowired
    TableRepository tableRepository;

    @Test
    public void testMethod() {
        //TODO 目前需要手动输入要遍历的包名，需要改成一个函数
        long packageId = 564;
        List<Table> tables = tableRepository.findTablesOfSamePackage(packageId);
        System.out.println(tables);

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

    public double addWeightBySamePackage(double weight){
        //TODO 根据频率增加weight
        return weight + 5;
    }

}
