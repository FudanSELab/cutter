package cn.icedsoul.cutter.domain.bo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class SplitGranularity {

    private int curServiceNum;//当前拆分方案的微服务数量
    private int maxServiceNum;//最多微服务数量=表的数量

    public int getCurServiceNum() {
        return curServiceNum;
    }

    public void setCurServiceNum(int curServiceNum) {
        this.curServiceNum = curServiceNum;
    }

    public int getMaxServiceNum() {
        return maxServiceNum;
    }

    public void setMaxServiceNum(int maxServiceNum) {
        this.maxServiceNum = maxServiceNum;
    }

}
