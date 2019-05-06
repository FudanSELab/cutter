package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.MethodCall;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:38
 */
public interface MethodCallRepository extends Neo4jRepository<MethodCall, Long> {
}
