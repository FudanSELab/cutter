package cn.icedsoul.cutter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Sql {
    @Id
    @GeneratedValue
    private Long id;
    private String databaseName;
    private String sql;

    @Relationship(type = "EXECUTE", direction = Relationship.INCOMING)
    private Set<Method> callMethods;

    @Relationship(type = "CONTAIN")
    private Set<Table> tables;

    public Sql(String databaseName, String sql){
        this.databaseName = databaseName;
        this.sql = sql;
        this.callMethods = new HashSet<>();
        this.tables = new HashSet<>();
    }
}
