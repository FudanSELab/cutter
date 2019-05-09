package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Method;
import cn.icedsoul.cutter.relation.BaseRelation;
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
    /**
     * 查询某条trace所有方法调用,按照Order字段升序排列
     * @param method
     * @param traceId
     * @return
     */
    List<MethodCall> findAllByMethodAfterAndTraceIdOrderByOrderAsc(Method method, Long traceId);

    /**
     * 查询某条trace所有大于某level的方法调用
     * @param method
     * @param traceId
     * @param level
     * @return
     */
    List<MethodCall> findAllByMethodAfterAndTraceIdAndLevelGreaterThan(Method method, Long traceId, Integer level);

    /**
     * 获取所有traceId
     * @return
     */
    @Query("match ()-[r]-() where exists(r.traceId) return distinct r.traceId")
    List<Long> findALLTrace();


    /**
     * 获取某条Trace某个level所有方法调用
     * @param traceId
     * @param level
     * @return
     */
    List<MethodCall> findAllByTraceIdAndLevelOrderByOrder(Long traceId, Integer level);

    /**
     *
     * @param traceId
     * @return
     */
    List<MethodCall> findAllByTraceId(Long traceId);
}
