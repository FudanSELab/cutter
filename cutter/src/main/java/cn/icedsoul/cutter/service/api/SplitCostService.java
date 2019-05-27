package cn.icedsoul.cutter.service.api;

import java.util.List;

public interface SplitCostService {

    int[] getSplitCost(List<List<Long>> tableGroups);

}
