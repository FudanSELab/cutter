package cn.icedsoul.cutter.service.api;

import cn.icedsoul.cutter.domain.bo.SplitCost;
import cn.icedsoul.cutter.domain.bo.SplitProposal;

import java.util.List;

public interface SplitCostService {

    SplitCost getSplitCost(List<List<Long>> tableGroups);

    SplitProposal getCodeSplitProposal();

}
