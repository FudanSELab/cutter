package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.Method;
import cn.icedsoul.cutter.domain.Sql;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:53
 */
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
}
