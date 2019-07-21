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

}
