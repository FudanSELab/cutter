package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.Execute;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:39
 */
@Repository
public interface ExecuteRepository extends Neo4jRepository<Execute, Long> {
    /**
     * 获取某条Trace某个Level所有execute
     * @param traceId
     * @param level
     * @return
     */
    List<Execute> findAllByTraceIdAndLevelOrderByOrder(Long traceId, Integer level);

    List<Execute> findAllByTraceIdOrderByOrder(Long traceId);

}
