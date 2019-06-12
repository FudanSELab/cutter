package cn.icedsoul.cutter.util;

import cn.icedsoul.cutter.domain.bo.Request;
import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.domain.po.Package;
import cn.icedsoul.cutter.domain.po.Sql;
import cn.icedsoul.cutter.relation.BaseRelation;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:29
 */
public class Common {

    public static Method ENTRY = null;
    public static Sql TMP_SQL = null;
    public static Package ROOT = null;
    public static Map<Method, Request> requests = new HashMap<>();
    public static Map<Long, Double> sqlWeight = new HashMap<>();
    public static Map<Long, Double> traceWeight = new HashMap<>();
    public static Map<String, Double> scenarioWeight = new HashMap<>();

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean isNull(Object o){
        if(o == null) {
            return true;
        }
        return false;
    }

    public static boolean isNullString(String s){
        return s == null || s.equals("");
    }

    public static String getTime(){
        return simpleDateFormat.format(new Date());
    }

    public static Double round(Double d){
        DecimalFormat df = new DecimalFormat("#.0000");
        return Double.valueOf(df.format(d));
    }

    public static String format(Double d){
        return String.format("%.2f", d);
    }

    public static void addSqlWeight(Sql sql, BaseRelation baseRelation){
        if(sqlWeight.containsKey(sql.getId())){
            sqlWeight.put(sql.getId(), sqlWeight.get(sql.getId()) + baseRelation.getScenarioFrequency());
        }
        else {
            sqlWeight.put(sql.getId(), baseRelation.getScenarioFrequency());
        }
    }

    public static void addTraceWeight(BaseRelation baseRelation){
        if(traceWeight.containsKey(baseRelation.getTraceId())){
            traceWeight.put(baseRelation.getTraceId(), traceWeight.get(baseRelation.getTraceId()) + baseRelation.getScenarioFrequency());
        }
        else {
            traceWeight.put(baseRelation.getTraceId(), baseRelation.getScenarioFrequency());
        }
    }

    public static void addScenarioWeight(BaseRelation baseRelation){
        if(scenarioWeight.containsKey(baseRelation.getScenarioId())){
            scenarioWeight.put(baseRelation.getScenarioId(), scenarioWeight.get(baseRelation.getScenarioId()) + baseRelation.getScenarioFrequency());
        }
        else {
            scenarioWeight.put(baseRelation.getScenarioId(), baseRelation.getScenarioFrequency());
        }
    }


}
