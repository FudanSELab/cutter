package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.domain.dto.CloseToRelation;
import cn.icedsoul.cutter.relation.CloseTo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface CloseToRepository extends Neo4jRepository<CloseTo, Long> {

    @Query("match (t1:Table{databaseName:{0},tableName:{1}})-[r:CLOSETO]-(t2:Table{databaseName:{2}, tableName:{3}}) " +
            "where r.level < {4} " +
            "with count(r) as numr " +
            "return toBoolean(numr <> 0)")
    boolean findCloseToBetweenTwoTablesAndLevelLessThan(String startDatabaseName, String startTableName,
                                                     String endDatabaseName, String endTableName, int level);

    @Query("match (t1:Table{databaseName:{0},tableName:{1}})-[r:CLOSETO{level:{4}}]-(t2:Table{databaseName:{2}, tableName:{3}})" +
            "return r.weight")
    List<Double> findCloseToByStartTableAndEndTableAndLevel(String startDatabaseName, String startTableName,
                                                    String endDatabaseName, String endTableName, int level);

    @Query("match (t1:Table{databaseName:{0},tableName:{1}})-[r:CLOSETO{level:{4}}]-(t2:Table{databaseName:{2}, tableName:{3}}) " +
            "set r.weight = {5} " +
            "return r.weight")
    double setWeight(String startDabaseName, String startTableName,
                      String endDatabaseName, String endTableName,
                      int level, double weight);


    /**
     * 取出一个Table相连的以其为起点的CLOSETO关系,目前考虑五层（同SQL、同TRACE、同SCENARIO、同MODULE）关系
     * @param nodeId
     * @return
     */
    @Query("match (n:Table)-[r:CLOSETO]->(t:Table) " +
            "where id(n)={0} and r.level <= 5 " +
            "return id(n) as startTableId, id(t) as endTableId, r.weight as weight, r.level as level")
    List<CloseToRelation> findCloseTosOfNode(long nodeId);

}
