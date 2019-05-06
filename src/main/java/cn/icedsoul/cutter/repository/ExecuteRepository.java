package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.Execute;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:39
 */
public interface ExecuteRepository extends Neo4jRepository<Execute, Long> {
}
