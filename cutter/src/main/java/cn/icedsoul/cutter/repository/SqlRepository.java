package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.po.Sql;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:26
 */
@Repository
public interface SqlRepository extends Neo4jRepository<Sql, Long> {
    /**
     * 查询指定sql语句
     * @param databaseName
     * @param sql
     * @return
     */
    Sql findByDatabaseNameAndAndSql(String databaseName, String sql);

    @Query("match (m:Method)-[r:EXECUTE]->(s:Sql) " +
            "where id(s)={0} and r.scenarioFrequency > 0 " +
            "return sum(r.scenarioFrequency)")
    double getSumSqlFrequencyBySqlId(long slqId);
}
