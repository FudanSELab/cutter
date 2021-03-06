package cn.icedsoul.cutter.domain.po;

import cn.icedsoul.cutter.util.CONSTANT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

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

    //scenario sharing degree
    private double ssd;
    //module sharing degree
    private double msd;

    private Set<Long> appearSql;
    private Set<Long> appearTrace;
    private Set<String> appearScenario;
    private Set<String> appearModule;

    public Table(String databaseName, String tableName){
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.ssd = 0;
        this.msd = 0;
        this.appearSql = new HashSet<>();
        this.appearTrace = new HashSet<>();
        this.appearScenario = new HashSet<>();
        this.appearModule = new HashSet<>();
    }

    public void addSql(Sql sql){
        this.appearSql.add(sql.getId());
    }

    public void addTrace(Long traceId){
        this.appearTrace.add(traceId);
    }

    public void addScenario(String scenarioId){
        this.appearScenario.add(scenarioId);
    }

    public void addModule(String moduleId){
        if(!moduleId.equals(CONSTANT.NO_MODULE_NAME)) {
            this.appearModule.add(moduleId);
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode() + this.databaseName.hashCode() + this.tableName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Table other = (Table) obj;
        return this.databaseName.equals(other.getDatabaseName()) && this.tableName.equals(other.getTableName());
    }

}
