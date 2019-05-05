package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.Person;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * @author IcedSoul
 * @date 19-5-5 上午10:54
 */
@RelationshipEntity(type = "FOLLOW")
public class PersonRelation {
    @StartNode
    Person start;
    @EndNode
    Person end;
}
