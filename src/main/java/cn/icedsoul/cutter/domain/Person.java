package cn.icedsoul.cutter.domain;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

/**
 * @author IcedSoul
 * @date 19-5-5 上午10:43
 */
@Data
public class Person {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private Integer age;

    private String other;
}
