package cn.icedsoul.cutter.domain;

import cn.icedsoul.cutter.relation.MethodCall;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:24
 */

@Data
@NoArgsConstructor
@NodeEntity
public class Method {
    @Id
    @GeneratedValue
    private Long id;
    private List<String> modifier;
    private String returnType;
    private String packageName;
    private String className;
    private String methodName;
    private List<String> params;

    @JsonIgnoreProperties("method")
    @Relationship(type = "METHOD_CALL", direction = Relationship.INCOMING)
    private Set<MethodCall> callMethods;

    @Relationship(type = "METHOD_CALL")
    private Set<Method> methods;

    @Relationship(type = "EXECUTE")
    private Set<Sql> sql;

    public Method(List<String> modifier, String returnType,  String packageName,  String className,String methodName, List<String> params){
        this.modifier = modifier;
        this.returnType = returnType;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.callMethods = new HashSet<>();
        this.methods = new HashSet<>();
        this.sql = new HashSet<>();
    }
}
