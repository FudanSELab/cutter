package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.queryresult.CloseToRelation;
import cn.icedsoul.cutter.relation.CloseTo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface CloseToRepository extends Neo4jRepository<CloseTo, Long> {

    @Query("match (t1:Table{databaseName:{0},tableName:{1}})-[r:CLOSETO]-(t2:Table{databaseName:{2}, tableName:{3}})" +
            "where r.level < {4}" +
            "return count(r)")
    int findCloseToBetweenTwoTablesAndLevelLessThan(String startDatabaseName, String startTableName,
                                                     String endDatabaseName, String endTableName, int level);

    @Query("match (t1:Table{databaseName:{0},tableName:{1}})-[r:CLOSETO{level:{4}}]-(t2:Table{databaseName:{2}, tableName:{3}})" +
            "return r.weight")
    List<Double> findCloseToByStartTableAndEndTableAndLevel(String startDatabaseName, String startTableName,
                                                    String endDatabaseName, String endTableName, int level);

    @Query("match (t1:Table{databaseName:{0},tableName:{1}})-[r:CLOSETO{level:{4}}]-(t2:Table{databaseName:{2}, tableName:{3}})" +
            "set r.weight = {5}" +
            "return r.weight")
    double setWeight(String startDabaseName, String startTableName,
                      String endDatabaseName, String endTableName,
                      int level, double weight);


    /**
     * 取出一个Table相连的以其为起点的CLOSETO关系
     * @param nodeId
     * @return
     */
    @Query("match (n:Table)-[r:CLOSETO]->(t:Table)" +
            "where id(n)={0}" +
            "return id(n) as startTableId, id(t) as endTableId, r.weight as weight")
    List<CloseToRelation> findCloseTosOfNode(long nodeId);

}
