package cn.icedsoul.cutter.repository;

import cn.icedsoul.cutter.queryresult.CloseToRelation;
import cn.icedsoul.cutter.relation.CloseTo;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface CloseToRepository extends Neo4jRepository<CloseTo, Long> {

    @Query("match (t1:Table)-[r:CLOSETO]-(t2:Table)" +
            "where t1.databaseName={0} and t1.tableName={1}" +
            "and t2.databaseName={2} and t2.tableName={3}" +
            "return r.weight")
    List<Double> findCloseToByStartTableAndEndTable(String startDatabaseName, String startTableName,
                                                     String endDatabaseName, String endTableName);

//    @Query("start t1=node({0}), t2=node({1})" +
//            "match (t1)-[r:CLOSETO]->(t2)" +
//            "return r")
//    List<CloseTo> findCloseToByStartTableAndEndTable(Table startTable, Table endTable);

    @Query("match (t1:Table)-[r:CLOSETO]-(t2:Table)" +
            "where t1.databaseName={0} and t1.tableName={1}" +
            "and t2.databaseName={2} and t2.tableName={3}" +
            "set r.weight = {4}" +
            "return r.weight")
    double setWeight(String startDabaseName, String startTableName,
                      String endDatabaseName, String endTableName,
                      double weight);


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
