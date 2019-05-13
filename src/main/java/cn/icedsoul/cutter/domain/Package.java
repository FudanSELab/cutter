package cn.icedsoul.cutter.domain;

import cn.icedsoul.cutter.relation.PackageContain;
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
 * @date 19-5-9 下午3:44
 */
@Data
@NoArgsConstructor
@NodeEntity
public class Package {
    @Id
    @GeneratedValue
    private Long id;
    private String fullPackageName;
    private String packageName;

    @JsonIgnoreProperties("class")
    @Relationship(type = "PACKAGE_CONTAIN", direction = Relationship.INCOMING)
    private Set<PackageContain> packageContains;

    @Relationship(type = "PACKAGE_CONTAIN")
    private Set<Package> packages;

    @Relationship(type = "CLASS_CONTAIN")
    private Set<Class> classes;

    public Package(String packageName, String fullPackageName){
        this.packageName = packageName;
        this.fullPackageName = fullPackageName;
        this.packageContains = new HashSet<>();
        this.packages = new HashSet<>();
        this.classes = new HashSet<>();
    }
}
