package cn.icedsoul.cutter.domain.bo;

import cn.icedsoul.cutter.domain.po.Class;
import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.domain.po.Sql;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
public class SplitDetail {

    private Map<Integer, List<Sql>> groupBySql;
    private Map<Integer, List<Method>>  groupByMethod;
    private Map<Integer, List<Class>>  groupByClass;


    public Map<Integer, List<Sql>> getGroupBySql() {
        return groupBySql;
    }

    public void setGroupBySql(Map<Integer, List<Sql>> groupBySql) {
        this.groupBySql = groupBySql;
    }

    public Map<Integer, List<Method>> getGroupByMethod() {
        return groupByMethod;
    }

    public void setGroupByMethod(Map<Integer, List<Method>> groupByMethod) {
        this.groupByMethod = groupByMethod;
    }

    public Map<Integer, List<Class>> getGroupByClass() {
        return groupByClass;
    }

    public void setGroupByClass(Map<Integer, List<Class>> groupByClass) {
        this.groupByClass = groupByClass;
    }




}
