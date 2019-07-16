package cn.icedsoul.cutter.domain.bo;

import cn.icedsoul.cutter.domain.po.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class SplitResult {

    private Map<Integer, List<Table>> splitProposal;
    private SplitCost splitCost;

    public Map<Integer, List<Table>> getSplitProposal() {
        return splitProposal;
    }

    public void setSplitProposal(Map<Integer, List<Table>> splitProposal) {
        this.splitProposal = splitProposal;
    }

    public SplitCost getSplitCost() {
        return splitCost;
    }

    public void setSplitCost(SplitCost splitCost) {
        this.splitCost = splitCost;
    }
}
