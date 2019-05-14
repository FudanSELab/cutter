package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.Class;
import cn.icedsoul.cutter.domain.Method;
import cn.icedsoul.cutter.domain.Package;
import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.relation.*;
import cn.icedsoul.cutter.repository.*;
import cn.icedsoul.cutter.service.api.HandleDataService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.icedsoul.cutter.util.CONSTANT.*;
import static cn.icedsoul.cutter.util.Common.*;

/**
 * @author IcedSoul
 * @date 19-5-6 上午11:02
 */
@Log
@Service
public class HandleDataServiceImpl implements HandleDataService {

    @Resource
    private MethodRepository methodRepository;

    @Autowired
    SqlRepository sqlRepository;

    @Autowired
    TableRepository tableRepository;

    @Autowired
    MethodCallRepository methodCallRepository;

    @Autowired
    ExecuteRepository executeRepository;

    @Autowired
    ContainRepository containRepository;

    @Autowired
    PackageRepository packageRepository;

    @Autowired
    PackageContainRepository packageContainRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    ClassContainRepository classContainRepository;

    @Autowired
    MethodContainRepository methodContainRepository;


    private List<BaseRelation> relations = new ArrayList<>();

    @Override
    public void handleData(String fileName) {
        clearDatabase();
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                handleSingleLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buildTree();
    }

    private void buildTree() {
        Map<Long, List<BaseRelation>> trace = getLocalRelations();
        for(Long traceId : trace.keySet()){
            log.info("[NOTICE]: This TraceId is " + traceId);
            List<BaseRelation> relations = trace.get(traceId);
            Map<Integer, List<BaseRelation>> levelRelation = relations.stream().collect(Collectors.groupingBy(BaseRelation::getLevel));
            List<Integer> levels = new ArrayList<>(levelRelation.keySet());
            Collections.sort(levels);
            List<BaseRelation> lastRelations =  levelRelation.get(0);
            methodCallRepository.save((MethodCall)lastRelations.get(0));
            for(int i = 1; i < levels.size(); i++){
                List<BaseRelation> relationList = levelRelation.get(i);
                Collections.sort(relationList);
                for(BaseRelation relation: relationList){
                    int mark = 0;
                    while(mark + 1 < lastRelations.size() && relation.getOrder() > lastRelations.get(mark + 1).getOrder()){
                       mark++;
                    }
                    changeParent(lastRelations.get(mark), relation);
                }
                lastRelations = relationList;
            }
        }

    }

    private void handleSingleLine(String line){
        if(line.length() <= 0){
            log.info("This line is blank!");
            return;
        }
        String[] lines = line.split(";");
        if(DAT_START.equals(lines[0])){
            ENTRY = methodRepository.findByPackageNameAndClassNameAndMethodName("", "", "Entry");
            if(isNull(ENTRY)) {
                ENTRY = new Method();
                ENTRY.setPackageName("");
                ENTRY.setClassName("");
                ENTRY.setMethodName("Entry");
                ENTRY = methodRepository.save(ENTRY);
            }
            TMP_SQL = sqlRepository.findByDatabaseNameAndAndSql("Temp", "Temp");
            if(isNull(TMP_SQL)){
                TMP_SQL = new Sql("Temp", "Temp");
                TMP_SQL = sqlRepository.save(TMP_SQL);
            }
            ROOT = packageRepository.findByFullPackageName("Root");
            if(isNull(ROOT)){
                ROOT = new Package("Root", "Root");
                ROOT = packageRepository.save(ROOT);
            }
            return;
        }
        BaseRelation baseRelation = new BaseRelation(Long.valueOf(lines[4]), lines[3], lines[10], lines[11],
                Double.valueOf(lines[12]), lines[13], Integer.valueOf(lines[9]), Integer.valueOf(lines[8]));
        if(NODE_TYPE_CLASS_FUNCTION.equals(lines[7])) {
            Method method = handleMethod(lines[2]);
            handleMethodCall(method, Long.valueOf(lines[5]), Long.valueOf(lines[6]), baseRelation);
        }
        else if(NODE_TYPE_SQL.equals(lines[7])) {
            handleExecuteSql(Long.valueOf(lines[1]), lines[2], baseRelation);
        }
        else if(NODE_TYPE_DATABASE_TABLE.equals(lines[7])) {
            handleTable(lines[2], baseRelation);
        }
        else {
            log.info("Parse Error!");
        }

    }

    private Method handleMethod(String methodInfo) {
        methodInfo = methodInfo.substring(0, methodInfo.indexOf(")") + 1);
        String[] methods = methodInfo.split(" ");
        List<String> modifier = new ArrayList<>();
        Collections.addAll(modifier, Arrays.copyOfRange(methods, 0, methods.length - 2));
        List<String> params = new ArrayList<>();
        String returnType = methods[methods.length - 2];
        String methodNameAndParams = methods[methods.length - 1];
        String methodNameAll = methodNameAndParams.substring(0, methodNameAndParams.indexOf('('));
        String methodName = methodNameAll.substring(methodNameAll.lastIndexOf('.') + 1);
        String packageAndClassName = methodNameAll.substring(0, methodNameAll.lastIndexOf("."));
        String packageName = packageAndClassName.substring(0, packageAndClassName.lastIndexOf("."));
        String className = packageAndClassName.substring(packageAndClassName.lastIndexOf(".") + 1);
        Collections.addAll(params, methodNameAndParams.substring(methodNameAndParams.indexOf('(') + 1, methodNameAndParams.indexOf(')')).split(","));
        Method method = methodRepository.
                findByModifierAndReturnTypeAndPackageNameAndClassNameAndMethodNameAndParams(modifier, returnType, packageName, className, methodName, params);
        if(isNull(method)) {
            Method newMethod = new Method(modifier, returnType, packageName, className, methodName, params);
            Method returnMethod = methodRepository.save(newMethod);
            buildIndex(returnMethod);
            return returnMethod;
        }
        return method;
    }

    /**
     * 处理方法调用关系时按照以下逻辑：
     *  1. 若为当前trace第一条记录，则父节点为Entry，直接插入
     *  2. 若当前trace已经有记录，则寻找到正确的位置进行插入，插入结点后调整树结构，使其保持正常
     *
     * @param method
     * @param startTime
     * @param endTime
     * @param baseRelation
     */
    private void handleMethodCall(Method method, Long startTime, Long endTime, BaseRelation baseRelation){
        MethodCall methodCall = new MethodCall(baseRelation);
        methodCall.setStartTime(startTime);
        methodCall.setEndTime(endTime);
        methodCall.setCalledMethod(method);
        methodCall.setMethod(ENTRY);
        relations.add(methodCall);
        log.info("[NOTICE]: I'm handling MethodCall.");
    }

    private void handleExecuteSql(Long executeTime, String dbAndsql, BaseRelation baseRelation){
        String[] content = dbAndsql.split(":", 2);
        Sql sql = sqlRepository.findByDatabaseNameAndAndSql(content[0], content[1]);
        if(isNull(sql)){
            sql = new Sql(content[0], content[1]);
            sql = sqlRepository.save(sql);
        }
        Execute execute = new Execute(baseRelation);
        execute.setMethod(ENTRY);
        execute.setSql(sql);
        execute.setExecuteTime(executeTime);
        relations.add(execute);
        log.info("[NOTICE]: I'm handling Execute.");
    }

    private void handleTable(String dbAndTable, BaseRelation baseRelation){
        String[] content = dbAndTable.split(":", 2);
        Table table = tableRepository.findByDatabaseNameAndAndTableName(content[0], content[1].toLowerCase());
        if(isNull(table)){
            table = new Table(content[0], content[1].toLowerCase());
            table = tableRepository.save(table);
        }
        Contain contain = new Contain(baseRelation);
        contain.setSql(TMP_SQL);
        contain.setTable(table);
        relations.add(contain);
        log.info("[NOTICE]: I'm handling Table.");
    }

    private void clearDatabase() {
        methodRepository.clearDatabase();
        log.info("[NOTICE]: Clear all data.");
    }


//    private Map<Long, List<BaseRelation>> getRelations(){
//        log.info("[NOTICE]: Start get trace relations:" + getTime());
//        List<MethodCall> methodCalls = (List<MethodCall>) methodCallRepository.findAll();
//        List<Execute> executes = (List<Execute>) executeRepository.findAll();
//        List<Contain> contains = (List<Contain>) containRepository.findAll();
//        log.info("[NOTICE]: End get trace relations:" + getTime());
//        List<BaseRelation> traceRelations = new ArrayList<>();
//        traceRelations.addAll(methodCalls);
//        traceRelations.addAll(executes);
//        traceRelations.addAll(contains);
//        return traceRelations.stream().collect(Collectors.groupingBy(BaseRelation::getTraceId));
//    }

    private Map<Long, List<BaseRelation>> getLocalRelations(){
        return this.relations.stream().collect(Collectors.groupingBy(BaseRelation::getTraceId));
    }


    private void changeParent(BaseRelation parent, BaseRelation child){
        if(child instanceof MethodCall){
            MethodCall childMethodCall = (MethodCall) child;
            MethodCall parentMethodCall = (MethodCall) parent;
            childMethodCall.setMethod(parentMethodCall.getCalledMethod());
            methodCallRepository.save(childMethodCall);
        }
        else if(child instanceof Execute){
            Execute childExecute = (Execute) child;
            MethodCall parentMethodCall = (MethodCall) parent;
            childExecute.setMethod(parentMethodCall.getCalledMethod());
            executeRepository.save(childExecute);
        }
        else if(child instanceof Contain){
            Contain childContain = (Contain) child;
            Execute parentExecute = (Execute) parent;
            childContain.setSql(parentExecute.getSql());
            containRepository.save(childContain);
        }
    }

    private void buildIndex(Method method) {
        String[] packageNames = method.getPackageName().split("\\.");
        Package lastPackage = ROOT;
        String currentName = "";
        for (String packageName : packageNames) {
            if(isNullString(currentName)){
                currentName += packageName;
            }
            else {
                currentName += "." + packageName;
            }
            Package aPackage = packageRepository.findByFullPackageName(currentName);
            if (isNull(aPackage)) {
                aPackage = new Package(packageName, currentName);
                aPackage = packageRepository.save(aPackage);
                PackageContain packageContain = new PackageContain();
                packageContain.setParentPackage(lastPackage);
                packageContain.setAPackage(aPackage);
                packageContainRepository.save(packageContain);
            }
            lastPackage = aPackage;
        }
        Class clazz = classRepository.findByPackageNameAndClassName(method.getPackageName(), method.getClassName());
        if (isNull(clazz)){
            clazz = new Class(method.getPackageName(), method.getClassName());
            clazz = classRepository.save(clazz);
            ClassContain classContain = new ClassContain();
            classContain.setParentPackage(lastPackage);
            classContain.setAClass(clazz);
            classContainRepository.save(classContain);
        }
        MethodContain methodContain = new MethodContain();
        methodContain.setParentClass(clazz);
        methodContain.setMethod(method);
        methodContainRepository.save(methodContain);
    }
}
