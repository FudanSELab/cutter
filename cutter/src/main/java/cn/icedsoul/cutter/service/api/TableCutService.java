package cn.icedsoul.cutter.service.api;

import java.util.List;
import java.util.Map;

public interface TableCutService {

    Map<Integer, List<String>> cutTable(int k);
}
