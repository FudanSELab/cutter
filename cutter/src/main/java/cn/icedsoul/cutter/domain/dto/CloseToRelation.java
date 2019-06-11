package cn.icedsoul.cutter.domain.dto;

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
}
