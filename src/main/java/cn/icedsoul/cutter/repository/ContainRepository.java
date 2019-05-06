package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.Contain;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:39
 */
public interface ContainRepository extends Neo4jRepository<Contain, Long> {
}
