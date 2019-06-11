package cn.icedsoul.cutter.domain.bo;

import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.relation.BaseRelation;
import cn.icedsoul.cutter.relation.Contain;
import cn.icedsoul.cutter.relation.Execute;
import cn.icedsoul.cutter.relation.MethodCall;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author IcedSoul
 * @date 19-6-5 下午2:40
 */
@Data
public class Request{
    private Method entry;
    private List<TraceType> traces;
    private boolean containTable;

    public Request(){
        this.traces = new ArrayList<>();
        this.containTable = false;
    }

    public void addTrace(List<BaseRelation> baseRelations){
        MethodCall methodCall = (MethodCall) baseRelations.get(0);
        for (TraceType traceType : this.traces){
            if(isSamePathTrace(traceType.getRelations(), baseRelations)){
                traceType.addTrace(methodCall.getTraceId());
                return;
            }
        }
        TraceType traceType = new TraceType();
        traceType.setRelations(baseRelations);
        traceType.addTrace(methodCall.getTraceId());
        boolean containTable = false;
        for(BaseRelation baseRelation: baseRelations){
            if(baseRelation instanceof Contain){
                containTable = true;
                break;
            }
        }
        traceType.setContainTable(containTable);
        if(containTable){
            this.containTable = true;
        }
        this.traces.add(traceType);
    }

    private boolean isSamePathTrace(List<BaseRelation> trace1, List<BaseRelation> trace2){
        if(trace1.size() != trace2.size()){
            return false;
        }
        for(int i = 0; i < trace1.size(); i++){
            BaseRelation baseRelation = trace1.get(i);
            BaseRelation baseRelation1 = trace2.get(i);
            if ((baseRelation instanceof MethodCall) && (baseRelation1 instanceof MethodCall)){
                MethodCall methodCall = (MethodCall) baseRelation;
                MethodCall methodCall1 = (MethodCall) baseRelation1;
                if(!methodCall.getCalledMethod().equals(methodCall1.getCalledMethod())){
                    return false;
                }
            }
            else if ((baseRelation instanceof Execute) && (baseRelation1 instanceof Execute)){
                Execute execute = (Execute) baseRelation;
                Execute execute1 = (Execute) baseRelation1;
                if(!execute.getSql().equals(execute1.getSql())){
                    return false;
                }

            }
            else if ((baseRelation instanceof Contain) && (baseRelation1 instanceof Contain)){
                Contain contain = (Contain) baseRelation;
                Contain contain1 = (Contain) baseRelation1;
                if(!contain.getTable().equals(contain1.getTable())){
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return true;
    }

    public boolean containTrace(Long tracesId){
        for(TraceType traceType : this.traces){
            if(traceType.containTrace(tracesId)){
                return true;
            }
        }
        return false;
    }
}

