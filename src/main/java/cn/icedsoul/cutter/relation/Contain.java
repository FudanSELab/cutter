package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:53
 */
@EqualsAndHashCode(exclude = {"sql", "table"}, callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "CONTAIN")
public class Contain extends BaseRelation {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Sql sql;

    @EndNode
    private Table table;

    public Contain(BaseRelation baseRelation){
        super(baseRelation.getTraceId(), baseRelation.getSessionId(),
                baseRelation.getScenarioId(), baseRelation.getScenarioName(),
                baseRelation.getLevel(), baseRelation.getOrder());
    }

    @Override
    public String toString(){
        return table.getDatabaseName() + ":" + table.getTableName();
    }
}
