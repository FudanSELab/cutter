package cn.icedsoul.cutter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:24
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@NodeEntity
public class Method {
    @Id
    @GeneratedValue
    private Long id;
    private String modifier;
    private String returnType;
    private String methodName;
    private List<String> params;

    @Relationship(type = "METHOD_CALL", direction = Relationship.INCOMING)
    private Set<Method> callMethods;

    @Relationship(type = "METHOD_CALL")
    private Set<Method> calledMethods;

    @Relationship(type = "EXECUTE")
    private Set<Sql> sql;
}
