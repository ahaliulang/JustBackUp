package com.ahaliulang.work;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    //根据日期取得星期几
    public static String getWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String week = sdf.format(new Date());
        return week;
    }

    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(new Date());
    }

    public static boolean canAfterPunch(){
        try {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//如2016-08-10 20:40
            long from = simpleFormat.parse((getDate() + " " + getTime())).getTime();
            long to = simpleFormat.parse((getDate() + " 18:00")).getTime();
            return (from - to) >= 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean canBeforePunch(){
        try {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//如2016-08-10 20:40
            long from = simpleFormat.parse((getDate() + " " + getTime())).getTime();
            long to = simpleFormat.parse((getDate() + " 10:30")).getTime();
            return (from - to) < 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}
