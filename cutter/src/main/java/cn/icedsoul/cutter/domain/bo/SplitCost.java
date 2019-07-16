package cn.icedsoul.cutter.domain.bo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class SplitCost {

    private int[] splitNum;//0：sqlNum, 1:methodNum, 2:classNum
    private Map sqlToSplit = new HashMap<>();
    private Map methodToSplit = new HashMap<>();
    private Map classToSplit = new HashMap<>();

    public int[] getSplitNum() {
        return splitNum;
    }

    public void setSplitNum(int[] splitNum) {
        this.splitNum = splitNum;
    }

    public Map getSqlToSplit() {
        return sqlToSplit;
    }

    public void setSqlToSplit(Map sqlToSplit) {
        this.sqlToSplit = sqlToSplit;
    }

    public Map getMethodToSplit() {
        return methodToSplit;
    }

    public void setMethodToSplit(Map methodToSplit) {
        this.methodToSplit = methodToSplit;
    }

    public Map getClassToSplit() {
        return classToSplit;
    }

    public void setClassToSplit(Map classToSplit) {
        this.classToSplit = classToSplit;
    }

}
