package cn.icedsoul.cutter.service.impl;

import cn.icedsoul.cutter.domain.Method;
import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.relation.BaseRelation;
import cn.icedsoul.cutter.relation.Contain;
import cn.icedsoul.cutter.relation.Execute;
import cn.icedsoul.cutter.relation.MethodCall;
import cn.icedsoul.cutter.repository.*;
import cn.icedsoul.cutter.service.api.HandleDataService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static cn.icedsoul.cutter.util.CONSTANT.*;
import static cn.icedsoul.cutter.util.Common.*;

/**
 * @author IcedSoul
 * @date 19-5-6 上午11:02
 */
@Log
@Service
public class HandleDataServiceImpl implements HandleDataService {

    @Autowired
    MethodRepository methodRepository;

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


    @Override
    public void handleData(String fileName) {
        File file = new File(fileName);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                handleSingleLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSingleLine(String line){
        if(line.length() <= 0){
            log.info("This line is blank!");
            return;
        }
        String[] lines = line.split(";");
        if(DAT_START.equals(lines[0])){
            Method method = methodRepository.findByMethodName("Entry");
            if(method == null) {
                method = new Method();
                method.setMethodName("Entry");
                methodRepository.save(method);
            }
            return;
        }
        BaseRelation baseRelation = new BaseRelation(Long.valueOf(lines[4]), lines[3], lines[10], lines[11], Integer.valueOf(lines[9]), Integer.valueOf(lines[8]));
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
        String methodName = methodNameAndParams.substring(0, methodNameAndParams.indexOf('('));
        Collections.addAll(params, methodNameAndParams.substring(methodNameAndParams.indexOf('(') + 1, methodNameAndParams.indexOf(')')).split(","));
        Method method = methodRepository.findByModifierAndReturnTypeAndMethodNameAndParams(modifier, returnType, methodName, params);
        if(isNull(method)) {
            Method newMethod = new Method(modifier, returnType, methodName, params);
            return methodRepository.save(newMethod);
        }
        return method;
    }

    private void handleMethodCall(Method method, Long startTime, Long endTime, BaseRelation baseRelation){
        MethodCall methodCall = new MethodCall(baseRelation);
        methodCall.setStartTime(startTime);
        methodCall.setEndTime(endTime);
        methodCall.setMethod(methodRepository.findByMethodName("Entry"));
        methodCall.setCalledMethod(method);
        methodCallRepository.save(methodCall);
    }

    private void handleExecuteSql(Long executeTime, String dbAndsql, BaseRelation baseRelation){
        String[] content = dbAndsql.split(":", 2);
        Sql sql = sqlRepository.findByDatabaseNameAndAndSql(content[0], content[1]);
        if(isNull(sql)){
            sql = new Sql(content[0], content[1]);
            sql = sqlRepository.save(sql);
        }
        Execute execute = new Execute(baseRelation);
        execute.setMethod(methodRepository.findByMethodName("Entry"));
        execute.setSql(sql);
        execute.setExecuteTime(executeTime);
        executeRepository.save(execute);
    }

    private void handleTable(String dbAndTable, BaseRelation baseRelation){
        String[] content = dbAndTable.split(":", 2);
        Table table = tableRepository.findByDatabaseNameAndAndTableName(content[0], content[1]);
        if(isNull(table)){
            table = new Table(content[0], content[1]);
            table = tableRepository.save(table);
        }
//        Contain contain = new Contain(baseRelation);
//        contain.setSql();
//        contain.setTable(table);
//        containRepository.save(contain);
    }

}
