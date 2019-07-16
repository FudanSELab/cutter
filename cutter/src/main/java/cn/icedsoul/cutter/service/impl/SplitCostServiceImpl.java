package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.bo.SplitCost;
import cn.icedsoul.cutter.domain.bo.SplitProposal;
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
    Map<Long, Integer> tableGroupMap;

    Map<Sql, Set<Integer>> sqlToSplit ;
    Map<Method, Set<Integer>> methodToSplit ;
    Map<Class, Set<Integer>> classToSplit ;

    Map<String, Set<Integer>> sqlToSplitResult ;
    Map<String, Set<Integer>> methodToSplitResult ;
    Map<String, Set<Integer>> classToSplitResult ;

    Map<Integer, Set<Sql>>  groupBySql;
    Map<Integer, Set<Method>>  groupByMethod;
    Map<Integer, Set<Class>>  groupByClass;

    @Override
    public SplitCost getSplitCost(List<List<Long>> tgs) {
        //initial work
        sqlToSplit = new HashMap<>();
        methodToSplit = new HashMap<>();
        classToSplit = new HashMap<>();
        tableGroupMap = new HashMap<Long, Integer>();

        groupBySql = new HashMap<>();
        groupByMethod = new HashMap<>();
        groupByClass = new HashMap<>();

        sqlToSplitResult = new HashMap<>();
        methodToSplitResult = new HashMap<>();
        classToSplitResult = new HashMap<>();

        initTableGroupMap(tgs);

        Iterator<Sql> sqlIterator = sqlRepository.findAll().iterator();
        while(sqlIterator.hasNext()){
            Sql sql = sqlIterator.next();
            List<Table> tableList = tableRepository.findTablesBySql(sql.getDatabaseName(), sql.getSql());
            Set<Long> tableIdList = tableList.stream().map(Table::getId).collect(Collectors.toSet());
            if(tableIdList != null){
                Set<Integer> cs = getClusterSet(tableIdList);
                for(int clusterNum: cs){
                    groupBySql.putIfAbsent(clusterNum, new HashSet<Sql>());
                    groupBySql.get(clusterNum).add(sql);
                }
                if(cs.size() > 1){
                    sqlToSplit.put(sql, cs);
                }
            }
        }

        Iterator<Method> methodIterator = methodRepository.findAll().iterator();
        while(methodIterator.hasNext()){
            Method method = methodIterator.next();
            Set<Long> tableIdList = method.getTables();
            if(tableIdList != null){
                Set<Integer> cs = getClusterSet(tableIdList);
                for(int clusterNum: cs){
                    groupByMethod.putIfAbsent(clusterNum, new HashSet<Method>());
                    groupByMethod.get(clusterNum).add(method);
                }
                if(cs.size() > 1){
                    methodToSplit.put(method, cs);
                }
            }
        }

        Iterator<Class> classIterator = classRepository.findAll().iterator();
        while(classIterator.hasNext()){
            Class c = classIterator.next();
            Set<Long> tableIdList = c.getTables();
            if(tableIdList != null && tableIdList.size()> 1){
                Set<Integer> cs = getClusterSet(tableIdList);
                for(int clusterNum: cs){
                    groupByClass.putIfAbsent(clusterNum, new HashSet<Class>());
                    groupByClass.get(clusterNum).add(c);
                }
                if(cs.size() > 1){
                    classToSplit.put(c, cs);
                }
            }
        }

        System.out.println("--sql");
        for(Sql s: sqlToSplit.keySet()){
            sqlToSplitResult.put(s.getSql(), sqlToSplit.get(s));
            System.out.println("SQL ID [" + s.getId() + "] : " + sqlToSplit.get(s));
        }
        System.out.println("--method");
        for(Method m: methodToSplit.keySet()){
            methodToSplitResult.put(m.getClassName()+"."+m.getMethodName(), methodToSplit.get(m));
            System.out.println(m.getClassName()+"."+m.getMethodName() + " : " + methodToSplit.get(m));
        }
        System.out.println("--class");
        for(Class s: classToSplit.keySet()){
            classToSplitResult.put(s.getPackageName()+"."+s.getClassName(), classToSplit.get(s));
            System.out.println(s.getPackageName()+"."+s.getClassName() + " : " + classToSplit.get(s));
        }
        System.out.println("----拆分开销：");
        System.out.println("sqlNum:"+sqlToSplit.size());
        System.out.println("methodNum:"+methodToSplit.size());
        System.out.println("classNum:"+classToSplit.size());

        //0:sqlNumToSplit 1:methodNumToSplit 2:classNumToSplit
        int[] result = new int[]{sqlToSplit.size(), methodToSplit.size(),classToSplit.size()};

        SplitCost cost = new SplitCost(result, sqlToSplitResult, methodToSplitResult, classToSplitResult);

        return cost;
    }

    @Override
    public SplitProposal getCodeSplitProposal() {
        SplitProposal sp = new SplitProposal(groupBySql, groupByMethod, groupByClass);
        return sp;
    }


    //为了方便，先计算从tableid到所属group的映射
    private void initTableGroupMap(List<List<Long>> tgs){
        tableGroups = tgs;
        for(int i = 0; i < tableGroups.size(); i++){
            List<Long> list = tableGroups.get(i);
            for(Long l: list){
//                tableGroupMap.put(l,i);
                //groupNum从1开始，所以index+1
                tableGroupMap.put(l,i + 1);
            }
        }
    }


    /**
     * 返回tableIdList中的这些table属于哪几个group
     * @param tableIdList
     * @return
     */
    private Set<Integer> getClusterSet(Set<Long> tableIdList){
        Set<Integer> groupIds = new HashSet<>();
        for(Long t: tableIdList){
            int groupNum = tableGroupMap.get(t);
            groupIds.add(groupNum);
        }
        return groupIds;
    }

}
