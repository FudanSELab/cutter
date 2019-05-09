package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Table;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:27
 */
@Repository
public interface TableRepository extends Neo4jRepository<Table, Long> {
    Table findByDatabaseNameAndAndTableName(String databaseName, String tableName);
}
