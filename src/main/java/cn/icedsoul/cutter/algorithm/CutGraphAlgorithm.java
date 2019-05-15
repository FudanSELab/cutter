package cn.icedsoul.cutter.algorithm;

import cn.icedsoul.cutter.domain.Table;

import java.util.List;
import java.util.Map;

public interface CutGraphAlgorithm {

    Map<Integer, List<Integer>> calculate();
}
