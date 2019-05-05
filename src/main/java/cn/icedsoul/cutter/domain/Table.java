package cn.icedsoul.cutter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Table {
    private String databaseName;
    private String tableName;

    @Relationship(type = "CONTAIN", direction = Relationship.INCOMING)
    private Set<Sql> sql;
}
