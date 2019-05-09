package cn.icedsoul.cutter.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

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
    private String packageName;


}
