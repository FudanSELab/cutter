package cn.icedsoul.cutter.service.api;

import cn.icedsoul.cutter.domain.bo.ShareTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SharingDegreeService {

    void calculateSharingDegree();

    List<Set<ShareTable>> shareCalculate(int k);
}
