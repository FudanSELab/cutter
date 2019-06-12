package cn.icedsoul.cutter.queryresult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.annotation.QueryResult;

@Data
@AllArgsConstructor
@NoArgsConstructor
@QueryResult
public class CloseToRelation {

    long startTableId;
    long endTableId;
    double weight;
    //1:同sql 2：同trace 3：同scenario 4:同package 5：同module
    int level;
}
