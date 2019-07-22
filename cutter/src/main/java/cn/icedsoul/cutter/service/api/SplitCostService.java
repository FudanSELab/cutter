package cn.icedsoul.cutter.service.api;

import cn.icedsoul.cutter.domain.bo.SplitCost;
import cn.icedsoul.cutter.domain.bo.SplitDetail;
import cn.icedsoul.cutter.domain.bo.SplitNode;


import java.util.List;
import java.util.Map;

public interface SplitCostService {

    SplitCost getSplitCost(List<List<Long>> tableGroups);

    SplitDetail getCodeSplitDetail();

    Map<Integer, List<SplitNode>> getCodeSplitDetailTree();

    List<SplitNode> getNoTableTree();

    //仅计算拆分总分，不用计算总拆分代价，仅用于最优方案的计算
    double simpleGetSplitCost(List<List<Long>> tableGroups);
}
