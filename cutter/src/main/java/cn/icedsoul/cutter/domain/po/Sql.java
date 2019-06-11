package cn.icedsoul.cutter.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.HashSet;
import java.util.Objects;
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
    private Set<Long> tables;

//    @JsonIgnoreProperties("sql")
//    @Relationship(type = "EXECUTE", direction = Relationship.INCOMING)
//    private Set<Execute> executes;
//
//    @Relationship(type = "CONTAIN")
//    private Set<Table> tables;
    public void addTable(Table table){
        this.tables.add(table.getId());
    }
    public Sql(String databaseName, String sql){
        this.databaseName = databaseName;
        this.sql = sql;
        this.tables = new HashSet<>();
//        this.executes = new HashSet<>();
//        this.tables = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sql sql1 = (Sql) o;
        return Objects.equals(databaseName, sql1.databaseName) &&
                Objects.equals(sql, sql1.sql);
    }

    @Override
    public int hashCode() {
        return Objects.hash(databaseName, sql);
    }
}
