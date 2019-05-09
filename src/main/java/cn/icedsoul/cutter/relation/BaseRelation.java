package cn.icedsoul.cutter.relation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基本关系
 *
 * @author IcedSoul
 * @date 19-5-5 下午4:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRelation implements Comparable<BaseRelation> {
    private Long traceId;
    private String sessionId;
    private String scenarioId;
    private String scenarioName;
    private Double scenarioFrequency;
    private Integer level;
    private Integer order;

    @Override
    public int compareTo(BaseRelation o) {
        if(this.level.compareTo(o.level) == 0){
            return this.order.compareTo(o.order);
        }
        return this.level.compareTo(o.level);
    }
}
