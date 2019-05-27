package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.repository.MethodCallRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import cn.icedsoul.cutter.service.api.SharingDegreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class SharingDegreeServiceImpl implements SharingDegreeService {

    @Autowired
    TableRepository tableRepository;
    @Autowired
    MethodCallRepository methodCallRepository;

    Map<String, Double> ssdMap = new HashMap();
    Map<String, Double> msdMap = new HashMap();

    @Override
    public void calculateSharingDegree() {
        //总场景数量
        int scenarioNum = methodCallRepository.countScenarioNum();
        //总模块数量
        int moduleNum = methodCallRepository.countModuleNum();
        //得到所有table
        Iterator<Table> tableIterator = tableRepository.findAll().iterator();
        while(tableIterator.hasNext()){
            Table table = tableIterator.next();
            int scenarioNumOfTable = tableRepository.countScenarioNumByTableId(table.getId());
            double ssd = (double)scenarioNumOfTable/(double)scenarioNum;
            tableRepository.setSSDByTableId(table.getId(), ssd);
            ssdMap.put(table.getTableName(), ssd);

            int moduleNumOfTable = tableRepository.countModuleNumByTableId(table.getId());
            double msd = (double)moduleNumOfTable/(double)moduleNum;
            tableRepository.setMSDByTableId(table.getId(), msd);
            msdMap.put(table.getTableName(), msd);
        }
        System.out.println("-----共享度-----");
        System.out.println("tableName\tssd\tmsd");
        for(String s:ssdMap.keySet()){
            System.out.println(s+" | "+ssdMap.get(s)+" | "+msdMap.get(s));
        }
    }


}
