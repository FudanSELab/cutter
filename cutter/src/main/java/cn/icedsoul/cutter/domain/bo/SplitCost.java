package cn.icedsoul.cutter.domain.bo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class SplitCost {

    private int[] splitNum;//0：sqlNum, 1:methodNum, 2:classNum
    private Map sqlToSplitResult = new HashMap<>();
    private Map methodToSplitResult = new HashMap<>();
    private Map classToSplitResult = new HashMap<>();
    private double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int[] getSplitNum() {
        return splitNum;
    }

    public void setSplitNum(int[] splitNum) {
        this.splitNum = splitNum;
    }

    public Map getSqlToSplitResult() {
        return sqlToSplitResult;
    }

    public void setSqlToSplitResult(Map sqlToSplitResult) {
        this.sqlToSplitResult = sqlToSplitResult;
    }

    public Map getMethodToSplitResult() {
        return methodToSplitResult;
    }

    public void setMethodToSplitResult(Map methodToSplitResult) {
        this.methodToSplitResult = methodToSplitResult;
    }

    public Map getClassToSplitResult() {
        return classToSplitResult;
    }

    public void setClassToSplitResult(Map classToSplitResult) {
        this.classToSplitResult = classToSplitResult;
    }

}
