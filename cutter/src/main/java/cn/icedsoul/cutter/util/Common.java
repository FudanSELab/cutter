package cn.icedsoul.cutter.util;

import cn.icedsoul.cutter.domain.Method;
import cn.icedsoul.cutter.domain.Package;
import cn.icedsoul.cutter.domain.Sql;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author IcedSoul
 * @date 19-5-6 下午2:29
 */
public class Common {

    public static Method ENTRY = null;
    public static Sql TMP_SQL = null;
    public static Package ROOT = null;

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
}
