package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Method;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:26
 */
public interface MethodRepository extends Neo4jRepository<Method, Long> {
}
