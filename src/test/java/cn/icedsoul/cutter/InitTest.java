package cn.icedsoul.cutter;

import cn.icedsoul.cutter.domain.Method;
import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import cn.icedsoul.cutter.relation.Contain;
import cn.icedsoul.cutter.relation.Execute;
import cn.icedsoul.cutter.relation.MethodCall;
import cn.icedsoul.cutter.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InitTest {
    @Autowired
    MethodRepository methodRepository;
    @Autowired
    SqlRepository sqlRepository;
    @Autowired
    TableRepository tableRepository;
    @Autowired
    ContainRepository containRepository;
    @Autowired
    ExecuteRepository executeRepository;
    @Autowired
    MethodCallRepository methodCallRepository;


    @Test
    public void testMethod(){
        //clean up
        methodRepository.deleteAll();
        sqlRepository.deleteAll();
        tableRepository.deleteAll();
        containRepository.deleteAll();
        executeRepository.deleteAll();
        methodCallRepository.deleteAll();

        Method m0 = new Method();//源点

        Method m1 = new Method();
        m1.setModifier(Arrays.asList(new String[]{"public"}));
        m1.setReturnType("int");
        m1.setMethodName("com.thinkgem.jeesite.modules.sys.dao.LogService.insert");
        m1.setParams(Arrays.asList(new String[]{"java.lang.Object"}));

        Method m2 = new Method();
        m2.setModifier(Arrays.asList(new String[]{"public"}));
        m2.setReturnType("int");
        m2.setMethodName("com.thinkgem.jeesite.modules.sys.dao.LogDao.insert");
        m2.setParams(Arrays.asList(new String[]{"java.lang.Object"}));
        methodRepository.save(m0);
        methodRepository.save(m1);
        methodRepository.save(m2);

        Sql s1 = new Sql();
        s1.setDatabaseName("jeesite");
        s1.setSql("select * from t1, t2 where t1.name = t2.t1Name");
        Sql s2 = new Sql();
        s2.setDatabaseName("jeesite");
        s2.setSql("select * from t3");
        Sql s3 = new Sql();
        s3.setDatabaseName("jeesite");
        s3.setSql("select * from t4");
        sqlRepository.save(s1);
        sqlRepository.save(s2);
        sqlRepository.save(s3);

        Table t1 = new Table();
        t1.setDatabaseName("jeesite");
        t1.setTableName("t1");
        Table t2 = new Table();
        t2.setDatabaseName("jeesite");
        t2.setTableName("t2");
        Table t3 = new Table();
        t3.setDatabaseName("jeesite");
        t3.setTableName("t3");
        Table t4 = new Table();
        t4.setDatabaseName("jeesite");
        t4.setTableName("t4");
        tableRepository.save(t1);
        tableRepository.save(t2);
        tableRepository.save(t3);
        tableRepository.save(t4);

        MethodCall mc0 = new MethodCall();
        mc0.setMethod(m0);
        mc0.setCalledMethod(m1);
        mc0.setStartTime(1556542563313242900L);
        mc0.setEndTime(1556542563313242900L);
        mc0.setTraceId(1L);
        mc0.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        mc0.setScenarioName("scenario1");
        mc0.setOrder(0);
        mc0.setLevel(0);

        MethodCall mc1 = new MethodCall();
        mc1.setMethod(m1);
        mc1.setCalledMethod(m2);
        mc1.setStartTime(1556542563313242900L);
        mc1.setEndTime(1556542563313242900L);
        mc1.setTraceId(1L);
        mc1.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        mc1.setScenarioName("scenario1");
        mc1.setOrder(3);
        mc1.setLevel(1);
        methodCallRepository.save(mc0);
        methodCallRepository.save(mc1);

        Execute e1 = new Execute();
        e1.setMethod(m1);
        e1.setSql(s3);
        e1.setExecuteTime(1556542563313262800L);
        e1.setTraceId(1L);
        e1.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        e1.setScenarioName("scenario1");
        e1.setOrder(1);
        e1.setLevel(1);

        Execute e2 = new Execute();
        e2.setMethod(m2);
        e2.setSql(s1);
        e2.setExecuteTime(1556542563313262800L);
        e2.setTraceId(1L);
        e2.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        e2.setScenarioName("scenario1");
        e2.setOrder(4);
        e2.setLevel(2);

        Execute e3 = new Execute();
        e3.setMethod(m2);
        e3.setSql(s2);
        e3.setExecuteTime(1556542563313262800L);
        e3.setTraceId(1L);
        e3.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        e3.setScenarioName("scenario1");
        e3.setOrder(7);
        e3.setLevel(2);
        executeRepository.save(e1);
        executeRepository.save(e2);
        executeRepository.save(e3);

        Contain c1 = new Contain();
        c1.setSql(s1);
        c1.setTable(t1);
        c1.setTraceId(1L);
        c1.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        c1.setScenarioName("scenario1");
        c1.setOrder(5);
        c1.setLevel(3);

        Contain c2 = new Contain();
        c2.setSql(s1);
        c2.setTable(t2);
        c2.setTraceId(1L);
        c2.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        c2.setScenarioName("scenario1");
        c2.setOrder(6);
        c2.setLevel(3);

        Contain c3 = new Contain();
        c3.setSql(s2);
        c3.setTable(t3);
        c3.setTraceId(1L);
        c3.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        c3.setScenarioName("scenario1");
        c3.setOrder(4);
        c3.setLevel(3);

        Contain c4 = new Contain();
        c4.setSql(s3);
        c4.setTable(t4);
        c4.setTraceId(1L);
        c4.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        c4.setScenarioName("scenario1");
        c4.setOrder(2);
        c4.setLevel(2);
        containRepository.save(c1);
        containRepository.save(c2);
        containRepository.save(c3);
        containRepository.save(c4);

        //scenario2
        Method m12 = new Method();
        m12.setModifier(Arrays.asList(new String[]{"public"}));
        m12.setReturnType("int");
        m12.setMethodName("com.thinkgem.jeesite.modules.sys.dao.UserService.insert");
        m12.setParams(Arrays.asList(new String[]{"java.lang.Object"}));
        Method m22 = new Method();
        m22.setModifier(Arrays.asList(new String[]{"public"}));
        m22.setReturnType("int");
        m22.setMethodName("com.thinkgem.jeesite.modules.sys.dao.UserDao.insert");
        m22.setParams(Arrays.asList(new String[]{"java.lang.Object"}));
        methodRepository.save(m0);
        methodRepository.save(m12);
        methodRepository.save(m22);

        Sql s12 = new Sql();
        s12.setDatabaseName("jeesite");
        s12.setSql("select * from t12, t22 where t12.name = t22.t1Name");
        sqlRepository.save(s12);

        Table t12 = new Table();
        t12.setDatabaseName("jeesite");
        t12.setTableName("t12");
        Table t22 = new Table();
        t22.setDatabaseName("jeesite");
        t22.setTableName("t22");
        tableRepository.save(t12);
        tableRepository.save(t22);

        MethodCall mc02 = new MethodCall();
        mc02.setMethod(m0);
        mc02.setCalledMethod(m12);
        mc02.setStartTime(1556542563313242900L);
        mc02.setEndTime(1556542563313242900L);
        mc02.setTraceId(2L);
        mc02.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        mc02.setScenarioName("scenario1");
        mc02.setOrder(0);
        mc02.setLevel(0);

        MethodCall mc12 = new MethodCall();
        mc12.setMethod(m12);
        mc12.setCalledMethod(m22);
        mc12.setStartTime(1556542563313242900L);
        mc12.setEndTime(1556542563313242900L);
        mc12.setTraceId(2L);
        mc12.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        mc12.setScenarioName("scenario1");
        mc12.setOrder(1);
        mc12.setLevel(1);
        methodCallRepository.save(mc02);
        methodCallRepository.save(mc12);

        Execute e12 = new Execute();
        e12.setMethod(m22);
        e12.setSql(s12);
        e12.setExecuteTime(1556542563313262800L);
        e12.setTraceId(2L);
        e12.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        e12.setScenarioName("scenario1");
        e12.setOrder(2);
        e12.setLevel(2);
        executeRepository.save(e12);

        Contain c12 = new Contain();
        c12.setSql(s12);
        c12.setTable(t12);
        c12.setTraceId(2L);
        c12.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        c12.setScenarioName("scenario1");
        c12.setOrder(3);
        c12.setLevel(3);

        Contain c22 = new Contain();
        c22.setSql(s12);
        c22.setTable(t22);
        c22.setTraceId(2L);
        c22.setScenarioId("a7f4b486950c444b9e933228e8d79ee8");
        c22.setScenarioName("scenario1");
        c22.setOrder(4);
        c22.setLevel(3);
        containRepository.save(c12);
        containRepository.save(c22);

    }
}
