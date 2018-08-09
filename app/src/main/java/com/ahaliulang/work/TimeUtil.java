package com.ahaliulang.work;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static boolean canAfterPunch() {
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

    public static boolean canBeforePunch() {
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

    public static boolean wakeUpTooLate() {
        try {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//如2016-08-10 20:40
            long from = simpleFormat.parse((getDate() + " " + getTime())).getTime();
            long to = simpleFormat.parse((getDate() + " 10:00")).getTime();
            return (from - to) < 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean goToBedTooLate(){
        try {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//如2016-08-10 20:40
            long from = simpleFormat.parse((getDate() + " " + getTime())).getTime();
            long to = simpleFormat.parse((getDate() + " 00:00")).getTime();
            return (from - to) < 5;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }



    public static int countDays() {
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse("2018-08-10"));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(new Date());
            return getDaysBetween(startCalendar, endCalendar);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int countDays(String dend){
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse("2018-08-10"));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse(dend));
            return getDaysBetween(startCalendar, endCalendar);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static int getDaysBetween(Calendar d1, Calendar d2) {
        if (d1.after(d2)) {
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
            d1 = (Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);// 得到当年的实际天数
                d1.add(Calendar.YEAR, 1);

            } while (d1.get(Calendar.YEAR) != y2);
        }
        return days;
    }


}
