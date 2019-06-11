package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.po.Class;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author IcedSoul
 * @date 19-5-9 下午4:46
 */
@Repository
public interface ClassRepository extends Neo4jRepository<Class, Long> {
    Class findByPackageNameAndClassName(String packageName, String className);
}
