package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.MethodCall;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:38
 */
public interface MethodCallRepository extends Neo4jRepository<MethodCall, Long> {

    @Query("match (a:Method) where a.methodName is null " +
            "with a " +
            "match (a)-[r:METHOD_CALL]->()" +
            "return distinct r.traceId")
    List<Long> listAllTrace();

    @Query("match (a:Method) where a.methodName is null " +
            "with a " +
            "match (a)-[r:METHOD_CALL]->()" +
            "return distinct r.scenarioId")
    List<String> listAllScenario();
}
