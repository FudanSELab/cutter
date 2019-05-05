package cn.icedsoul.cutter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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
    private String databaseName;
    private String sql;

    @Relationship(type = "EXECUTE", direction = Relationship.INCOMING)
    private Set<Method> callMethods;

    @Relationship(type = "CONTAIN", direction = Relationship.OUTGOING)
    private Set<Table> tables;
}
