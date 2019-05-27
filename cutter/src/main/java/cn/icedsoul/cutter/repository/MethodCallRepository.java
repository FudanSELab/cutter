package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.MethodCall;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:38
 */
@Repository
public interface MethodCallRepository extends Neo4jRepository<MethodCall, Long> {

    //包括了无效的trace，需要再判断处理
    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL]->() " +
            "return distinct r.traceId")
    List<Long> listAllTrace();

    //包括了无效的scenario，需要再判断处理
    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL]->() " +
            "return distinct r.scenarioId")
    List<String> listAllScenario();

    //包括了无效的module，需要再判断处理
    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL]->() " +
            "return distinct r.moduleName")
    List<String> listAllModule();

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL{traceId:{0}}]->() " +
            "return distinct r.scenarioFrequency")
    List<Double> getFrequencyByTraceId(long traceId);

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL{scenarioId:{0}}]->() " +
            "return distinct r.scenarioFrequency")
    List<Double> getFrequencyByScenarioId(String scenarioId);

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL{moduleName:{0}}]->() " +
            "where r.scenarioFrequency>0 " +
            "return sum(r.scenarioFrequency)")
    Double getFrequencyByModuleName(String moduleName);

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL]->() " +
            "where r.scenarioId<>'<no-scenario-id>' return count(distinct r.scenarioId)")
    int countScenarioNum();

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL]->() " +
            "where r.scenarioId<>'<no-scenario-id>' return count(distinct r.moduleName)")
    int countModuleNum();


}
