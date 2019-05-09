package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.Class;
import cn.icedsoul.cutter.domain.Method;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author IcedSoul
 * @date 19-5-9 下午4:27
 */
@EqualsAndHashCode(exclude = {"parentClass","method"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "METHOD_CONTAIN")
public class MethodContain {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Class parentClass;

    @EndNode
    private Method method;

    @Override
    public String toString(){
        return method.getMethodName();
    }
}
