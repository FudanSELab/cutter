package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.Package;
import cn.icedsoul.cutter.domain.Class;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author IcedSoul
 * @date 19-5-9 下午4:04
 */
@EqualsAndHashCode(exclude = {"parentPackage","aClass"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "CLASS_CONTAIN")
public class ClassContain {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Package parentPackage;

    @EndNode
    private Class aClass;
}
