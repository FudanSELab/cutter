package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.po.Package;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author IcedSoul
 * @date 19-5-9 下午3:57
 */
@EqualsAndHashCode(exclude = {"parentPackage","aPackage"})
@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "PACKAGE_CONTAIN")
public class PackageContain {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Package parentPackage;

    @EndNode
    private Package aPackage;

    @Override
    public String toString(){
        return aPackage.getPackageName();
    }
}
