package cn.icedsoul.cutter.domain;

import cn.icedsoul.cutter.relation.ClassContain;
import cn.icedsoul.cutter.relation.Execute;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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

    @JsonIgnoreProperties("class")
    @Relationship(type = "CLASS_CONTAIN", direction = Relationship.INCOMING)
    private Set<ClassContain> classContains;

    @Relationship(type = "METHOD_CONTAIN")
    private Set<Method> methods;

    public Class(String className){
        this.className = className;
        this.classContains = new HashSet<>();
        this.methods = new HashSet<>();
    }
}
