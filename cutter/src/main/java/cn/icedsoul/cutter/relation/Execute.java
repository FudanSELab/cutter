package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.domain.po.Sql;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:53
 */
@EqualsAndHashCode(exclude = {"method", "sql"}, callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "EXECUTE")
public class Execute extends BaseRelation {
    @Id
    @GeneratedValue
    private Long id;

    private Long executeTime;

    @StartNode
    private Method method;

    @EndNode
    private Sql sql;

    public Execute(BaseRelation baseRelation){
        super(baseRelation.getTraceId(), baseRelation.getSessionId(),
                baseRelation.getScenarioId(), baseRelation.getScenarioName(), baseRelation.getScenarioFrequency(),
                baseRelation.getModuleName(), baseRelation.getLevel(), baseRelation.getOrder());
    }

    @Override
    public String toString(){
        return sql.getDatabaseName() + ":" + sql.getSql();
    }
}
