package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Table;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:27
 */
public interface TableRepository extends Neo4jRepository<Table, Long> {
}
