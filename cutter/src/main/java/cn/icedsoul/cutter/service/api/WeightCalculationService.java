package cn.icedsoul.cutter.service.api;

public interface WeightCalculationService {

    void addSameScenarioWeight();

    void addSameTraceWeight();

    void addSameSqlWeight();

    void addSameModuleWeight();

    void addSamePackageWeight();

    void addWeight();

    void addSimilarWeight();

}
