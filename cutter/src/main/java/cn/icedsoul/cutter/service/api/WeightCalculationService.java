package cn.icedsoul.cutter.service.api;

import java.util.List;

public interface WeightCalculationService {

    void addSameScenarioWeight();

    void addSameTraceWeight();

    void addSameSqlWeight();

    void addSameModuleWeight();

    void addSamePackageWeight();

    void addWeight();

    List<Double[][]> addSimilarWeight();

}
