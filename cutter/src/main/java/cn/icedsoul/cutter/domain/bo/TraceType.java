package cn.icedsoul.cutter.domain.bo;

import cn.icedsoul.cutter.relation.BaseRelation;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author icedsoul
 */
@Data
public class TraceType{
    private boolean containTable;
    private List<BaseRelation> relations;
    private List<Long> traceIds;

    public TraceType(){
        this.relations = new ArrayList<>();
        this.traceIds = new ArrayList<>();
        this.containTable = false;
    }

    public void addTrace(Long traceId){
        this.traceIds.add(traceId);
    }

    public boolean containTrace(Long traceId){
        if(this.traceIds.contains(traceId)) {
            return true;
        }
        return false;
    }
}
