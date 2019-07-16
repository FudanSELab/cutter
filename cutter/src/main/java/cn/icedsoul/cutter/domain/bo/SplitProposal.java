package cn.icedsoul.cutter.domain.bo;

import cn.icedsoul.cutter.domain.po.Class;
import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.domain.po.Sql;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
public class SplitProposal {

    private Map<Integer, Set<Sql>> groupBySql;
    private Map<Integer, Set<Method>>  groupByMethod;
    private Map<Integer, Set<Class>>  groupByClass;

    public Map<Integer, Set<Sql>> getGroupBySql() {
        return groupBySql;
    }

    public void setGroupBySql(Map<Integer, Set<Sql>> groupBySql) {
        this.groupBySql = groupBySql;
    }

    public Map<Integer, Set<Method>> getGroupByMethod() {
        return groupByMethod;
    }

    public void setGroupByMethod(Map<Integer, Set<Method>> groupByMethod) {
        this.groupByMethod = groupByMethod;
    }

    public Map<Integer, Set<Class>> getGroupByClass() {
        return groupByClass;
    }

    public void setGroupByClass(Map<Integer, Set<Class>> groupByClass) {
        this.groupByClass = groupByClass;
    }

}
