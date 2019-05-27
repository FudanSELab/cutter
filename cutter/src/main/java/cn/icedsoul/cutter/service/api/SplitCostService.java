package cn.icedsoul.cutter.service.api;

import java.util.List;
import java.util.Map;

public interface SplitCostService {

    int[] getSplitCost(Map<Integer, List<Long>> tableGroups);

}
