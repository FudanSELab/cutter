package cn.icedsoul.cutter.domain.bo;

import cn.icedsoul.cutter.domain.po.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author IcedSoul
 * @date 19-6-10 上午10:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareTable implements Comparable<ShareTable>{
    private Table table;
    private Double sqlShare;
    private Double traceShare;
    private Double cTraceShare;
    private Double scenarioShare;
    private Double moduleShare;
    private Double requestShare;
    private Double traceTypeShare;
    private Double cRequestShare;
    private Double cTraceTypeShare;


    @Override
    public int compareTo(ShareTable o) {
        //TODO 优化
        if (this.scenarioShare + 0.8 * this.cTraceTypeShare + 0.2 * this.sqlShare < o.getScenarioShare() + 0.8 * o.getCTraceTypeShare() + 0.2 * o.getSqlShare()){
            return 1;
        }
        return -1;
    }


}
