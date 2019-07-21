package cn.icedsoul.cutter.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:24
 */

@Data
@NoArgsConstructor
@NodeEntity
public class Method implements Comparable{
    @Id
    @GeneratedValue
    private Long id;
    private List<String> modifier;
    private String returnType;
    private String packageName;
    private String className;
    private String methodName;
    private List<String> params;
    private Set<Long> tables;

//    @JsonIgnoreProperties("method")
//    @Relationship(type = "METHOD_CALL", direction = Relationship.INCOMING)
//    private Set<MethodCall> callMethods;
//
//    @JsonIgnoreProperties("class")
//    @Relationship(type = "METHOD_CONTAIN", direction = Relationship.INCOMING)
//    private Set<MethodContain> methodContains;
//
//    @Relationship(type = "METHOD_CALL")
//    private Set<Method> methods;
//
//    @Relationship(type = "EXECUTE")
//    private Set<Sql> sql;

    public void addTables(Set<Long> tables){
        this.tables.addAll(tables);
    }

    public Method(List<String> modifier, String returnType,  String packageName,  String className,String methodName, List<String> params){
        this.modifier = modifier;
        this.returnType = returnType;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.tables = new HashSet<>();
//        this.callMethods = new HashSet<>();
//        this.methodContains = new HashSet<>();
//        this.methods = new HashSet<>();
//        this.sql = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return Objects.equals(modifier, method.modifier) &&
                Objects.equals(returnType, method.returnType) &&
                Objects.equals(packageName, method.packageName) &&
                Objects.equals(className, method.className) &&
                Objects.equals(methodName, method.methodName) &&
                Objects.equals(params, method.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifier, returnType, packageName, className, methodName, params);
    }

    @Override
    public int compareTo(Object o) {
        return (this.className+"."+this.methodName).compareTo(((Method)o).getClassName()+"."+((Method)o).getMethodName());
    }
}
