package cn.icedsoul.cutter.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * @author IcedSoul
 * @date 19-5-9 下午3:45
 */
@Data
@NoArgsConstructor
@NodeEntity
public class Class {
    @Id
    @GeneratedValue
    private Long id;
    private String className;
    private String packageName;
    private Set<Long> tables;

//    @JsonIgnoreProperties("class")
//    @Relationship(type = "CLASS_CONTAIN", direction = Relationship.INCOMING)
//    private Set<ClassContain> classContains;
//
//    @Relationship(type = "METHOD_CONTAIN")
//    private Set<Method> methods;
    public void addTables(Set<Long> tables){
        this.tables.addAll(tables);
    }

    public Class(String packageName, String className){
        this.className = className;
        this.packageName = packageName;
        this.tables = new HashSet<>();
//        this.classContains = new HashSet<>();
//        this.methods = new HashSet<>();
    }
}
