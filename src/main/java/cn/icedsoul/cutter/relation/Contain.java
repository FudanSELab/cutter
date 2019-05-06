package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "CONTAIN")
public class Contain extends BaseRelation {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Sql sql;

    @EndNode
    private Table table;
}
