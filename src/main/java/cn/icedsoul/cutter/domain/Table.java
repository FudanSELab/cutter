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
 * @date 19-5-5 下午4:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Table{
    @Id
    @GeneratedValue
    private Long id;
    private String databaseName;
    private String tableName;

    @Relationship(type = "CONTAIN", direction = Relationship.INCOMING)
    private Set<Sql> sql;

    @Relationship(type = "CLOSETO")
    private Set<Table> closeTableList;

    public Table(String databaseName, String tableName){
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.sql = new HashSet<>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((tableName == null) ? 0 : tableName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Table other = (Table) obj;
        if (databaseName == null) {
            if (other.databaseName != null)
                return false;
        } else if (!databaseName.equals(other.databaseName))
            return false;
        if (tableName == null) {
            if (other.tableName != null)
                return false;
        } else if (!tableName.equals(other.tableName))
            return false;
        return true;
    }

}
