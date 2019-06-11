package cn.icedsoul.cutter.util;

import cn.icedsoul.cutter.domain.bo.Request;
import cn.icedsoul.cutter.domain.po.Method;
import cn.icedsoul.cutter.domain.po.Package;
import cn.icedsoul.cutter.domain.po.Sql;

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
}
