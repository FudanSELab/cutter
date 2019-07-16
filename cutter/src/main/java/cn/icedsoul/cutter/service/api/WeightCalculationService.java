package cn.icedsoul.cutter.service.api;

import cn.icedsoul.cutter.domain.bo.TwoWayRelation;
import cn.icedsoul.cutter.domain.po.Table;

import java.util.List;

public interface WeightCalculationService {

    void addSameScenarioWeight();

    void addSameTraceWeight();

    void addSameSqlWeight();

    void addSameModuleWeight();

    void addSamePackageWeight();

    void addWeight();

    List<Double[][]> addSimilarWeight();

    Double calculateSqlSimilarWithWeight(Table a, Table b);

    Double calculateTraceSimilarWithWeight(Table a, Table b);

    Double calculateScenarioSimilarWithWeight(Table a, Table b);

    TwoWayRelation calculateSqlSimilar(Table a, Table b);

    TwoWayRelation calculateTraceSimilar(Table a, Table b);

    TwoWayRelation calculateScenarioSimilar(Table a, Table b);


}
