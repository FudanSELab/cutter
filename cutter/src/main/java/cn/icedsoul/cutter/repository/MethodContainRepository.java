package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.MethodContain;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;


/**
 * @author IcedSoul
 * @date 19-5-9 下午4:48
 */
@Repository
public interface MethodContainRepository extends Neo4jRepository<MethodContain, Long> {
}
