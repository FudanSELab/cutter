package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.domain.po.Sql;
import cn.icedsoul.cutter.domain.po.Table;
import cn.icedsoul.cutter.domain.po.Class;
import cn.icedsoul.cutter.repository.ClassRepository;
import cn.icedsoul.cutter.repository.MethodRepository;
import cn.icedsoul.cutter.repository.SqlRepository;
import cn.icedsoul.cutter.repository.TableRepository;
import cn.icedsoul.cutter.service.api.SplitCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SplitCostServiceImpl implements SplitCostService {

    @Autowired
    TableRepository tableRepository;
    @Autowired
    SqlRepository sqlRepository;
    @Autowired
    MethodRepository methodRepository;
    @Autowired
    ClassRepository classRepository;

    List<List<Long>> tableGroups;
    Map<Long, Integer> tableGroupMap = new HashMap<Long, Integer>();


    @Override
    public int[] getSplitCost(List<List<Long>> tgs) {
        initTableGroupMap(tgs);

        Set<Sql> sqlToSplit = new HashSet<>();
        Set<Method> methodToSplit = new HashSet<>();
        Set<Class> classToSplit = new HashSet<>();
        Iterator<Sql> sqlIterator = sqlRepository.findAll().iterator();
        while(sqlIterator.hasNext()){
            Sql sql = sqlIterator.next();
            List<Table> tableList = tableRepository.findTablesBySql(sql.getDatabaseName(), sql.getSql());
            Set<Long> tableIdList = tableList.stream().map(Table::getId).collect(Collectors.toSet());
            if(tableIdList != null && tableIdList.size()> 1 && !allInOneGroup(tableIdList)){
                sqlToSplit.add(sql);
            }
        }

        Iterator<Method> methodIterator = methodRepository.findAll().iterator();
        while(methodIterator.hasNext()){
            Method method = methodIterator.next();
            Set<Long> tableIdList = method.getTables();
            if(tableIdList != null && tableIdList.size()> 1 && !allInOneGroup(tableIdList)){
                methodToSplit.add(method);
            }
        }

        Iterator<Class> classIterator = classRepository.findAll().iterator();
        while(classIterator.hasNext()){
            Class c = classIterator.next();
            Set<Long> tableIdList = c.getTables();
            if(tableIdList != null && tableIdList.size()> 1 && !allInOneGroup(tableIdList)){
                classToSplit.add(c);
            }
        }

        System.out.println("--sql");
        for(Sql s: sqlToSplit){
            System.out.println(s.getSql());
        }
        System.out.println("--method");
        for(Method m: methodToSplit){
            System.out.println(m.getClassName()+"."+m.getMethodName());
        }
        System.out.println("--class");
        for(Class s: classToSplit){
            System.out.println(s.getPackageName()+"."+s.getClassName());
        }
        System.out.println("----all");
        System.out.println("sqlNum:"+sqlToSplit.size());
        System.out.println("methodNum:"+methodToSplit.size());
        System.out.println("classNum:"+classToSplit.size());

        //0:sqlNumToSplit 1:methodNumToSplit 2:classNumToSplit
        int[] result = new int[]{sqlToSplit.size(), methodToSplit.size(),classToSplit.size()};
        return result;
    }


    //为了方便，先计算从tableid到所属group的映射
    private void initTableGroupMap(List<List<Long>> tgs){
        tableGroups = tgs;
        for(int i = 0; i < tableGroups.size(); i++){
            List<Long> list = tableGroups.get(i);
            for(Long l: list){
                tableGroupMap.put(l,i);
            }
        }
    }

    private boolean allInOneGroup(Set<Long> tableIdList){
        int index = -1;
        for(Long t: tableIdList){
            int groupNum = tableGroupMap.get(t);
            if(index == -1){
                index = groupNum;
            } else if(index != groupNum){
                return false;
            }
        }
        return true;
    }

}
