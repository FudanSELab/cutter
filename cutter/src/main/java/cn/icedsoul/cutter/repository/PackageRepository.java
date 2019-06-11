package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.po.Package;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-9 下午4:46
 */
@Repository
public interface PackageRepository extends Neo4jRepository<Package, Long> {
    Package findByFullPackageName(String fullPackageName);

    @Query("match (n:Package)-[:PACKAGE_CONTAIN]->(c:Package) where id(n)={0} return c")
    List<Package> findChildrenByPackageId(long id);

}
