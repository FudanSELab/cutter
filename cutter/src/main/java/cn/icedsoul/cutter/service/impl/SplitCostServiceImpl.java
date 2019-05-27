package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.repository.SqlRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import cn.icedsoul.cutter.service.api.SplitCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SplitCostServiceImpl implements SplitCostService {

    @Autowired
    TableRepository tableRepository;
    @Autowired
    SqlRepository sqlRepository;

    Map<Integer, List<Long>> tableGroups;

    @Override
    public int[] getSplitCost(Map<Integer, List<Long>> tgs) {
        tableGroups = tgs;
        Set<Sql> sqlToSplit = new HashSet<>();
        Set<Long> methodToSplit = new HashSet<>();
        Set<Long> classToSplit = new HashSet<>();
        Iterator<Sql> sqlList = sqlRepository.findAll().iterator();
        while(sqlList.hasNext()){
            Sql sql = sqlList.next();
            List<Table> tableList = tableRepository.findTablesBySql(sql.getDatabaseName(), sql.getSql());
            if(!allInOneGroup(tableList)){
                sqlToSplit.add(sql);
            }
        }



        //0:sqlNumToSplit 1:methodNumToSplit 2:classNumToSplit
        int[] result = new int[]{sqlToSplit.size(), methodToSplit.size(),classToSplit.size()};
        return result;
    }

    private boolean allInOneGroup(List<Table> tableList){
        int index = -1;
        for(Table t: tableList){
            if(index == -1){
//                index = ;
            }
        }

        return true;
    }

}
