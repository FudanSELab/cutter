package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.Method;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "METHOD_CALL")
public class MethodCall extends BaseRelation{
    @Id
    @GeneratedValue
    private Long id;

    private long startTime;
    private long endTime;

    @StartNode
    private Method method;

    @EndNode
    private Method calledMethod;
}
