package cn.icedsoul.cutter.relation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author IcedSoul
 * @date 19-5-5 下午4:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public
class BaseRelation {
    private Long traceId;
    private String sessionId;
    private String scenarioId;
    private String scenarioName;
    private Integer level;
    private Integer order;
}
