package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.po.Table;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:27
 */
@Repository
public interface TableRepository extends Neo4jRepository<Table, Long> {
    Table findByDatabaseNameAndAndTableName(String databaseName, String tableName);

    @Query("match (s:Sql{databaseName:{0},sql:{1}})-[:CONTAIN]->(t:Table)" +
            "return t")
    List<Table> findTablesBySql(String databaseName, String sql);

    @Query("match (:Sql)-[c:CONTAIN{traceId:{0}}]->(t:Table)" +
            "return distinct t" )
    List<Table> findTablesOfSameTrace(long traceId);

    @Query("match (:Sql)-[c:CONTAIN{scenarioId:{0}}]->(t:Table)" +
            "return distinct t" )
    List<Table> findTablesOfSameScenario(String scenarioId);

    @Query("match (:Sql)-[c:CONTAIN{moduleName:{0}}]->(t:Table)" +
            "return distinct t" )
    List<Table> findTablesOfSameModule(String moduleName);

    @Query("match (r:Package)-[:CONTAIN|:EXECUTE|:METHOD_CALL|:CLASS_CONTAIN|:METHOD_CONTAIN|:PACKAGE_CONTAIN*]->(t:Table) " +
            "where id(r)={0} return distinct t")
    List<Table> findTablesOfSamePackage(long packageId);

    @Query("match (t:Table) where id(t)={0} " +
            "set t.ssd={1} ")
    void setSSDByTableId(long tableId, double ssd);

    @Query("match (t:Table) where id(t)={0} " +
            "set t.msd={1} ")
    void setMSDByTableId(long tableId, double msd);

    @Query("match (t:Table)<-[c:CONTAIN]-(:Sql) " +
            "where id(t)={0} and c.scenarioId<>'<no-scenario-id>' " +
            "return count(distinct c.scenarioId)")
    int countScenarioNumByTableId(long tableId);

    @Query("match (t:Table)<-[c:CONTAIN]-(:Sql) " +
            "where id(t)={0} and c.scenarioId<>'<no-scenario-id>' " +
            "return count(distinct c.moduleName)")
    int countModuleNumByTableId(long tableId);

    //根据tableId获取跟这个table同sql的所有table
    //不去除scenarioFrequency<0的，因为只要有调用就需要拆分，就有拆分代价
//    @Query("match (s:Sql)-[:CONTAIN]->(t:Table) " +
//            "where id(t)={0} " +
//            "with s " +
//            "match (s)-[:CONTAIN]->(t:Table) " +
//            "return distinct(id(t))")
//    List<Long> listTablesOfSameSqlByTableId(long tableId);
}
