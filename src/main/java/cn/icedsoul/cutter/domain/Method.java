package cn.icedsoul.cutter.domain;

import cn.icedsoul.cutter.util.Common;
import lombok.AllArgsConstructor;
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
    private String methodName;
    private List<String> params;

    @Relationship(type = "METHOD_CALL", direction = Relationship.INCOMING)
    private Set<Method> callMethods;

    @Relationship(type = "METHOD_CALL")
    private Set<Method> calledMethods;

    @Relationship(type = "EXECUTE")
    private Set<Sql> sql;

    public Method(List<String> modifier, String returnType, String methodName, List<String> params){
        this.modifier = modifier;
        this.returnType = returnType;
        this.methodName = methodName;
        this.params = params;
        this.callMethods = new HashSet<>();
        this.calledMethods = new HashSet<>();
        this.sql = new HashSet<>();
    }

//    public void addMethodCall(Method callMethod, Method calledMethod){
//        if(Common.isNull(this.callMethods)){
//            this.callMethods = new HashSet<>();
//        }
//        if(Common.isNull(this.calledMethods)){
//            this.calledMethods = new HashSet<>();
//        }
//        this.callMethods.ad
//
//    }
}
