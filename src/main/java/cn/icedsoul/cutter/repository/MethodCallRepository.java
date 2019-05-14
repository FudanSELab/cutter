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

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL]->() " +
            "return distinct r.traceId")
    List<Long> listAllTrace();

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL]->() " +
            "return distinct r.scenarioId")
    List<String> listAllScenario();

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL]->() " +
            "return distinct r.moduleName")
    List<String> listAllModule();

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL{traceId:{0}}]->() " +
            "return distinct r.scenarioFrequency")
    List<Double> getTraceFrequencyByTraceId(long traceId);

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL{scenarioId:{0}}]->() " +
            "return distinct r.scenarioFrequency")
    List<Double> getTraceFrequencyByScenarioId(String scenarioId);

    @Query("match (a:Method{methodName:'Entry'})-[r:METHOD_CALL{moduleName:{0}}]->() " +
            "return sum(r.scenarioFrequency)")
    Double getModuleFrequencyByModuleName(String moduleName);

    @Query("match ()-[r{scenarioName:{0}}]->() " +
            "set r.scenarioFrequency={1}")
    void modifyScenarioFrequencyByScenarioName(String scenarioName, double scenarioFrequency);
}
