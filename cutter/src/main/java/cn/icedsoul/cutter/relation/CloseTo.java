package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.po.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RelationshipEntity(type = "CLOSETO")
public class CloseTo {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Table startTable;

    @EndNode
    private Table endTable;

    //1:同sql 2：同trace 3：同scenario 4:同package 5：同module
    private int level;

    //the weight between two tables
    private double weight;

}
