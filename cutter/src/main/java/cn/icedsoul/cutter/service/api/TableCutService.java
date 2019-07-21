package cn.icedsoul.cutter.service.api;

import cn.icedsoul.cutter.domain.bo.ShareTable;
import cn.icedsoul.cutter.domain.po.Table;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TableCutService {

    //Cut tables and then extract tables that sharing degree are high
    Map<Integer, List<Table>> cutTable(int k);

    //Extract tables that sharing degree are high and then cut table
    Map<Integer, List<Table>> cutTable2(int k);

    //Adjust the weight of tables that have high sharing degree and then cut table
    Map<Integer, List<Table>> cutTable3(int k);

    Map<Integer, List<Table>> realCut(int k, List<List<Table>> sharingClusters);

    Map<Integer, List<Table>> addService(int lastServiceNum);

    Map<Integer, List<Table>> reduceService(int lastServiceNum);

    int getCurServiceNum();

    int getMaxServiceNum();

}
