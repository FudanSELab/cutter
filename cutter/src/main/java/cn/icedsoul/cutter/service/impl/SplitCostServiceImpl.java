package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.bo.SplitCost;
import cn.icedsoul.cutter.domain.bo.SplitDetail;
import cn.icedsoul.cutter.domain.bo.SplitNode;
import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.domain.po.Package;
import cn.icedsoul.cutter.domain.po.Sql;
import cn.icedsoul.cutter.domain.po.Table;
import cn.icedsoul.cutter.domain.po.Class;
import cn.icedsoul.cutter.repository.*;
import cn.icedsoul.cutter.service.api.SplitCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SplitCostServiceImpl implements SplitCostService {

    @Autowired
    PackageRepository packageRepository;
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

    List<Long> sqlToSplitIds;
    List<Long> methodToSplitIds;

    Map<Integer, Set<Sql>>  groupBySql;
    Map<Integer, Set<Method>>  groupByMethod;
    Map<Integer, Set<Class>>  groupByClass;

    //排序后最终返回的详细代码拆分结果
    Map<Integer, List<Sql>> sortedGroupBySql;
    Map<Integer, List<Method>> sortedGroupByMethod;
    Map<Integer, List<Class>> sortedGroupByClass;

    //没有跟任何表关联的class和method
    Set<Class> noTableClassList;
    Set<Method> noTableMethodList;

    //计算拆分分数
    final static double A1 = 1, A2 = 0.5, A3 = 0.1;
    //构建包树形结构
    final int PACKAGE_LEVEL = 1, CLASS_LEVEL = 2, METHOD_LEVEL = 3, SQL_LEVEL = 4;


    //仅计算拆分多少个sql,method,class，不用计算总拆分代价，仅用于最优方案的计算
    @Override
    public double simpleGetSplitCost(List<List<Long>> tgs) {
        //initial work
        sqlToSplit = new HashMap<>();
        methodToSplit = new HashMap<>();
        classToSplit = new HashMap<>();
        tableGroupMap = new HashMap<Long, Integer>();

        sqlToSplitIds = new ArrayList<>();
        methodToSplitIds = new ArrayList<>();

        initTableGroupMap(tgs);

        Iterator<Sql> sqlIterator = sqlRepository.findAll().iterator();
        while(sqlIterator.hasNext()){
            Sql sql = sqlIterator.next();
            List<Table> tableList = tableRepository.findTablesBySql(sql.getDatabaseName(), sql.getSql());
            Set<Long> tableIdList = tableList.stream().map(Table::getId).collect(Collectors.toSet());
            if(tableIdList != null && tableIdList.size() > 1){
                Set<Integer> cs = getClusterSet(tableIdList);
                if(cs.size() > 1){
                    sqlToSplit.put(sql, cs);
                    sqlToSplitIds.add(sql.getId());
                }
            }
        }

        Iterator<Method> methodIterator = methodRepository.findAll().iterator();
        while(methodIterator.hasNext()){
            Method method = methodIterator.next();
            //和这个method直接相连的sql
            List<Long> sqlIds = sqlRepository.getSqlsByMethodId(method.getId());
            if(sqlIds != null && sqlIds.size() > 0){
                for(long id:sqlIds){
                    if(sqlToSplitIds.contains(id)){
                        Set<Integer> cs = getClusterSet(method.getTables());
                        methodToSplit.put(method, cs);
                        methodToSplitIds.add(method.getId());
                        break;
                    }
                }
            }
//            Set<Long> tableIdList = method.getTables();
//            if(tableIdList != null && tableIdList.size() > 1){
//                Set<Integer> cs = getClusterSet(tableIdList);
//                if(cs.size() > 1){
//                    methodToSplit.put(method, cs);
//                }
//            }
        }

        Iterator<Class> classIterator = classRepository.findAll().iterator();
        while(classIterator.hasNext()){
            Class c = classIterator.next();
            List<Long> methodIds = methodRepository.getMethodsByClassId(c.getId());
            if(methodIds != null && methodIds.size() > 0){
                for(long id:methodIds){
                    if(methodToSplitIds.contains(id)){
                        Set<Integer> cs = getClusterSet(c.getTables());
                        classToSplit.put(c, cs);
                        break;
                    }
                }
            }
//            Set<Long> tableIdList = c.getTables();
//            if(tableIdList != null && tableIdList.size() > 1){
//                Set<Integer> cs = getClusterSet(tableIdList);
//                if(cs.size() > 1){
//                    classToSplit.put(c, cs);
//                }
//            }
        }

        return calculateSplitScore(sqlToSplit.size(), methodToSplit.size(), classToSplit.size());
    }

    private double calculateSplitScore(int sqlNum, int methodNum, int classNum){
        return sqlNum * A1 + methodNum * A2 + classNum * A3;
    }

    @Override
    public SplitCost getSplitCost(List<List<Long>> tgs) {
        //initial work
        sqlToSplit = new HashMap<>();
        methodToSplit = new HashMap<>();
        classToSplit = new HashMap<>();

        sqlToSplitIds = new ArrayList<>();
        methodToSplitIds = new ArrayList<>();

        tableGroupMap = new HashMap<Long, Integer>();

        groupBySql = new HashMap<>();
        groupByMethod = new HashMap<>();
        groupByClass = new HashMap<>();

        sqlToSplitResult = new HashMap<>();
        methodToSplitResult = new HashMap<>();
        classToSplitResult = new HashMap<>();

        noTableClassList = new HashSet<>();
        noTableMethodList = new HashSet<>();

        initTableGroupMap(tgs);

        Iterator<Sql> sqlIterator = sqlRepository.findAll().iterator();
        while(sqlIterator.hasNext()){
            Sql sql = sqlIterator.next();
            List<Table> tableList = tableRepository.findTablesBySql(sql.getDatabaseName(), sql.getSql());
            Set<Long> tableIdList = tableList.stream().map(Table::getId).collect(Collectors.toSet());
            if(tableIdList != null && tableIdList.size() > 0){
                Set<Integer> cs = getClusterSet(tableIdList);
                for(int clusterNum: cs){
                    groupBySql.putIfAbsent(clusterNum, new HashSet<Sql>());
                    groupBySql.get(clusterNum).add(sql);
                }
                if(cs.size() > 1){
                    sqlToSplit.put(sql, cs);
                    sqlToSplitIds.add(sql.getId());
                }
            }
        }

        Iterator<Method> methodIterator = methodRepository.findAll().iterator();
        while(methodIterator.hasNext()){
            Method method = methodIterator.next();
            //只有直接跟要拆的sql相连的method要拆
            List<Long> sqlIds  = sqlRepository.getSqlsByMethodId(method.getId());
            boolean toSplit = false;
            if(sqlIds != null && sqlIds.size() > 0){
                for(long id:sqlIds){
                    if(sqlToSplitIds.contains(id)){
                        toSplit = true;
                        break;
                    }
                }
            }
            Set<Integer> cs = getClusterSet(method.getTables());
            for(int clusterNum: cs){
                groupByMethod.putIfAbsent(clusterNum, new HashSet<Method>());
                groupByMethod.get(clusterNum).add(method);
            }
            if(toSplit){
                methodToSplit.put(method, cs);
                methodToSplitIds.add(method.getId());
            } else {
                //去掉entry method!!!!!!!
                if( cs.size() <= 0 && ! method.getMethodName().equals("Entry")){
                    noTableMethodList.add(method);
                }
            }

//            Set<Long> tableIdList = method.getTables();
//            if(tableIdList != null && tableIdList.size() > 0){
//                Set<Integer> cs = getClusterSet(tableIdList);
//                for(int clusterNum: cs){
//                    groupByMethod.putIfAbsent(clusterNum, new HashSet<Method>());
//                    groupByMethod.get(clusterNum).add(method);
//                }
//                if(cs.size() > 1){
//                    methodToSplit.put(method, cs);
//                }
//            } else {
//                //去掉entry method!!!!!!!
//                if( ! method.getMethodName().equals("Entry")){
//                    noTableMethodList.add(method);
//                }
//            }
        }

        Iterator<Class> classIterator = classRepository.findAll().iterator();
        while(classIterator.hasNext()){
            Class c = classIterator.next();
            List<Long> methodIds = methodRepository.getMethodsByClassId(c.getId());
            boolean toSplit = false;
            if(methodIds != null && methodIds.size() > 0){
                for(long id:methodIds){
                    if(methodToSplitIds.contains(id)){
                        toSplit = true;
                        break;
                    }
                }
            }
            Set<Integer> cs = getClusterSet(c.getTables());
            for(int clusterNum: cs){
                groupByClass.putIfAbsent(clusterNum, new HashSet<Class>());
                groupByClass.get(clusterNum).add(c);
            }
            if(toSplit){
                classToSplit.put(c, cs);
            } else if(cs.size() <= 0){
                noTableClassList.add(c);
            }

//            Set<Long> tableIdList = c.getTables();
//            if(tableIdList != null && tableIdList.size() > 0){
//                Set<Integer> cs = getClusterSet(tableIdList);
//                for(int clusterNum: cs){
//                    groupByClass.putIfAbsent(clusterNum, new HashSet<Class>());
//                    groupByClass.get(clusterNum).add(c);
//                }
//                if(cs.size() > 1){
//                    classToSplit.put(c, cs);
//                }
//            } else {
//                noTableClassList.add(c);
//            }
        }

//        System.out.println("--sql");
        for(Sql s: sqlToSplit.keySet()){
            sqlToSplitResult.put(s.getSql(), sqlToSplit.get(s));
//            System.out.println("SQL ID [" + s.getId() + "] : " + sqlToSplit.get(s));
        }
//        System.out.println("--method");
        for(Method m: methodToSplit.keySet()){
            methodToSplitResult.put(m.getClassName()+"."+m.getMethodName(), methodToSplit.get(m));
//            System.out.println(m.getClassName()+"."+m.getMethodName() + " : " + methodToSplit.get(m));
        }
//        System.out.println("--class");
        for(Class s: classToSplit.keySet()){
            classToSplitResult.put(s.getPackageName()+"."+s.getClassName(), classToSplit.get(s));
//            System.out.println(s.getPackageName()+"."+s.getClassName() + " : " + classToSplit.get(s));
        }
        System.out.println("----拆分开销：");
        System.out.println("sqlNum:"+sqlToSplit.size());
        System.out.println("methodNum:"+methodToSplit.size());
        System.out.println("classNum:"+classToSplit.size());

        //0:sqlNumToSplit 1:methodNumToSplit 2:classNumToSplit
        int[] result = new int[]{sqlToSplit.size(), methodToSplit.size(),classToSplit.size()};

        //为了好看，排个序
        sortResult();
        sortDetail();

        return new SplitCost(result, sqlToSplitResult, methodToSplitResult, classToSplitResult,
                calculateSplitScore(sqlToSplitResult.size(), methodToSplitResult.size(), classToSplitResult.size()));
    }

    private void sortResult(){
        sqlToSplitResult = sortMap(sqlToSplitResult);
        methodToSplitResult = sortMap(methodToSplitResult);
        classToSplitResult = sortMap(classToSplitResult);
    }

    //对SplitResult进行排序
    private Map<String, Set<Integer>> sortMap(Map<String, Set<Integer>> map ){
        Set<Map.Entry<String, Set<Integer>>> entrySet = map.entrySet();
        List<Map.Entry<String, Set<Integer>>> list = new ArrayList<Map.Entry<String, Set<Integer>>>(entrySet);

        Collections.sort(list, new Comparator<Map.Entry<String, Set<Integer>>>() {
            @Override
            public int compare(Map.Entry<String, Set<Integer>> o1, Map.Entry<String, Set<Integer>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        LinkedHashMap<String, Set<Integer>> linkedHashMap = new LinkedHashMap<String, Set<Integer>>();
        for (Map.Entry<String, Set<Integer>> entry : list) {
            linkedHashMap.put(entry.getKey(), entry.getValue());
        }
        return linkedHashMap;
    }

    private void sortDetail(){
        //为了好看，排个序
        sortedGroupBySql = new HashMap<>();
        groupBySql.keySet().stream().forEach(key -> {
            List<Sql> sqls = groupBySql.get(key).stream().sorted(Comparator.comparing(Sql::getSql)).collect(Collectors.toList());
            sortedGroupBySql.put(key, sqls);
        });

        sortedGroupByMethod = new HashMap<>();
        groupByMethod.keySet().stream().forEach(key -> {
            List<Method> methods = groupByMethod.get(key).stream().sorted().collect(Collectors.toList());
            sortedGroupByMethod.put(key, methods);
        });

        sortedGroupByClass = new HashMap<>();
        groupByClass.keySet().stream().forEach(key -> {
            List<Class> classes = groupByClass.get(key).stream().sorted().collect(Collectors.toList());
            sortedGroupByClass.put(key, classes);
        });
    }

    @Override
    public SplitDetail getCodeSplitDetail() {
        SplitDetail sp = new SplitDetail(sortedGroupBySql, sortedGroupByMethod, sortedGroupByClass);
        return sp;
    }

    @Override
    public Map<Integer, List<SplitNode>> getCodeSplitDetailTree() {
        Map<Integer, List<SplitNode>> root = new HashMap<>();

        for(int key: sortedGroupByMethod.keySet()){
            //先把method存起来
            List<SplitNode> list1 = new ArrayList<>();
            List<Method> methods = sortedGroupByMethod.get(key);
            for (Method method: methods){
                list1.add(new SplitNode(method.getId(), method.getMethodName(), METHOD_LEVEL));
            }

            //！！！！不要删！！！！
            //确认每个sql属于哪个method
//            List<Sql> sqls = sortedGroupBySql.get(key);
//            for (Sql sql: sqls){
//                List<Method> sqlToMethods = methodRepository.findMethodsBySql(sql.getId());
//                for(Method stm: sqlToMethods){
//                    for(SplitNode sn: list1){
//                        if(stm.getId().equals(sn.getId())){
//                            sn.addNode(new SplitNode(sql.getId(), sql.getSql(), SQL_LEVEL));
//                        }
//                    }
//                }
//            }

            //list2把Class存起来
            List<SplitNode> list2 = new ArrayList<>();
            List<Class> classes = sortedGroupByClass.get(key);
            for(Class c: classes){
                list2.add(new SplitNode(c.getId(), c.getClassName(),CLASS_LEVEL));
            }
            // 确认每个method属于哪个class
            for(SplitNode sn: list1){
                Class c = classRepository.findClassByMethodId(sn.getId());
                for(SplitNode classNode: list2){
                    if(classNode.getId().equals(c.getId())){
                        classNode.addNode(sn);
                        break;
                    }
                }
            }

            SplitNode rootNode = buildTreeFromClass(list2);
            root.put(key, rootNode.getChildren());
        }
        return root;
    }

    /**
     * 以class一级的节点（里面已经有了method/sql）列表为输入，构建从root开始的目录树
     * @param list2 classNode
     * @return
     */
    private SplitNode buildTreeFromClass(List<SplitNode> list2){
        //先往上找一层，得到最底一层package
        Map<Long, SplitNode> map3 = new HashMap<>();
        for(SplitNode classNode: list2){
            Package p = packageRepository.findPackageByClassId(classNode.getId());
            if(map3.containsKey(p.getId())){
                map3.get(p.getId()).addNode(classNode);
            } else {
                SplitNode packageNode = new SplitNode(p.getId(), p.getPackageName(), PACKAGE_LEVEL);
                packageNode.addNode(classNode);
                map3.put(p.getId(), packageNode);
            }
        }
        //再从下到上收集所有必经的packageId
        Map<Long, SplitNode> tempMap = map3;
        Set<Long> packageIds = new HashSet<>(tempMap.keySet());
        while(true){
            Map<Long, SplitNode> map4 = new HashMap<>();
            for(long pid: tempMap.keySet()){
                SplitNode child = tempMap.get(pid);
                Package parent = packageRepository.findParentByPackageId(child.getId());
                if( ! "Root".equals(parent.getPackageName())){
                    packageIds.add(parent.getId());
                    if( ! map4.containsKey(parent.getId())){
                        SplitNode pNode = new SplitNode(parent.getId(), parent.getPackageName(), PACKAGE_LEVEL);
                        map4.put(parent.getId(), pNode);
                    }
                }
            }
            if(map4.isEmpty())break;
            tempMap = map4;
        }
        //从root package开始构树，添加进去的节点id必须在packageIds中
        Package rootPackage = packageRepository.findRoot();
        SplitNode rootNode = new SplitNode(rootPackage.getId(), rootPackage.getPackageName(), PACKAGE_LEVEL);
        rootNode = getPackageStructure(rootNode, packageIds, map3);

        return rootNode;
    }

    /**
     * 跟任何table都没有关联的方法/类的目录树
     * @return
     */
    @Override
    public List<SplitNode> getNoTableTree() {
        //先把Class存起来
        List<SplitNode> list1 = new ArrayList<>();
        for(Class c: noTableClassList){
            list1.add(new SplitNode(c.getId(), c.getClassName(),CLASS_LEVEL));
        }
        // 确认每个method属于哪个class
        for(Method method: noTableMethodList){
            Class c = classRepository.findClassByMethodId(method.getId());
            for(SplitNode classNode: list1){
                if(classNode.getId().equals(c.getId())){
                    classNode.addNode(new SplitNode(method.getId(), method.getMethodName(), METHOD_LEVEL));
                    break;
                }
            }
        }
        return buildTreeFromClass(list1).getChildren();
    }


    /**
     *
     * @param node
     * @param packageIds
     * @param map3 为存储了class/method/sql的最底一层的package
     * @return
     */
    private SplitNode getPackageStructure(SplitNode node, Set<Long> packageIds, Map<Long, SplitNode> map3){
        List<Package> children = packageRepository.findChildrenByPackageId(node.getId());
        if(children != null && children.size() > 0){
            for(Package p: children){
                if(packageIds.contains(p.getId())){
                    if(map3.containsKey(p.getId())){
                        node.addNode(map3.get(p.getId()));
                    } else {
                        node.addNode(getPackageStructure(new SplitNode(p.getId(), p.getPackageName(), PACKAGE_LEVEL), packageIds, map3));
                    }
                }
            }
        }
        return node;
    }


    //为了方便，先计算从tableid到所属group的映射
    private void initTableGroupMap(List<List<Long>> tgs){
        tableGroups = tgs;
        for(int i = 0; i < tableGroups.size(); i++){
            List<Long> list = tableGroups.get(i);
            for(Long l: list){
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
        if(tableIdList != null){
            for(Long t: tableIdList){
                int groupNum = tableGroupMap.get(t);
                groupIds.add(groupNum);
            }
        }
        return groupIds;
    }

}
