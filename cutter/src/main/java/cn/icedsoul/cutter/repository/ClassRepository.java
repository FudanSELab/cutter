package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.po.Class;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author IcedSoul
 * @date 19-5-9 下午4:46
 */
@Repository
public interface ClassRepository extends Neo4jRepository<Class, Long> {
    Class findByPackageNameAndClassName(String packageName, String className);

    /**
     * 寻找调用method的所有class
     */
    @Query("match (c:Class)-[r:METHOD_CONTAIN]->(m:Method) " +
            "where id(m)={0}" +
            "return c")
    Class findClassByMethodId(long methodId);
}
