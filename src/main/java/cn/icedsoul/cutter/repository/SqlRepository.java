package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Sql;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:26
 */
public interface SqlRepository extends Neo4jRepository<Sql, Long> {
    /**
     * 查询指定sql语句
     * @param databaseName
     * @param sql
     * @return
     */
    Sql findByDatabaseNameAndAndSql(String databaseName, String sql);
}
