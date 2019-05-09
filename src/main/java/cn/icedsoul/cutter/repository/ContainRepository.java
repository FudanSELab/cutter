package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.Contain;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:39
 */
@Repository
public interface ContainRepository extends Neo4jRepository<Contain, Long> {
    /**
     * 获取某条Trace某个Level所有Contain
     * @param traceId
     * @param level
     * @return
     */
    List<Contain> findAllByTraceIdAndLevelOrderByOrder(Long traceId, Integer level);
    List<Contain> findAllByTraceIdOrderByOrder(Long traceId);
}
