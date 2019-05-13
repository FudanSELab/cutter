package cn.icedsoul.cutter;

import cn.icedsoul.cutter.domain.Table;
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
        long packageId = 545;
        List<Table> tables = tableRepository.findTablesOfSamePackage(packageId);
        System.out.println(tables);

        for(int i = 0; i < tables.size(); i++) {
            for (int j = i + 1; j < tables.size(); j++) {

            }
        }

    }

}
