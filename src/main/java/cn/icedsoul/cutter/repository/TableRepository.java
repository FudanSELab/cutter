package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:27
 */
public interface TableRepository extends Neo4jRepository<Table, Long> {
    Table findByDatabaseNameAndAndTableName(String databaseName, String tableName);

    @Query("match (s:Sql)-[:CONTAIN]->(t:Table)" +
            "where s.databaseName={0} and s.sql={1}" +
            "return t")
    List<Table> findTablesBySql(String databaseName, String sql);


    @Query("match (:Sql)-[c:CONTAIN]->(t:Table)" +
            "where c.traceId={0} " +
            "return distinct t" )
    List<Table> findTablesOfSameTrace(long traceId);

    @Query("match (:Sql)-[c:CONTAIN]->(t:Table)" +
            "where c.scenarioId={0} " +
            "return distinct t" )
    List<Table> findTablesOfSameScenario(String scenarioId);

}
