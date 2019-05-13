package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Package;
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

    List<Package> findChildrenByPackageId(long id);

}
