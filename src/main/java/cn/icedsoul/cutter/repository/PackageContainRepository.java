package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.relation.PackageContain;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author IcedSoul
 * @date 19-5-9 下午4:47
 */
@Repository
public interface PackageContainRepository extends Neo4jRepository<PackageContain, Long> {
}
