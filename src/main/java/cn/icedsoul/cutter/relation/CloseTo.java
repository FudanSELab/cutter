package cn.icedsoul.cutter.relation;

import cn.icedsoul.cutter.domain.Sql;
import cn.icedsoul.cutter.domain.Table;
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

    //the weight between two tables
    private double weight;

}
