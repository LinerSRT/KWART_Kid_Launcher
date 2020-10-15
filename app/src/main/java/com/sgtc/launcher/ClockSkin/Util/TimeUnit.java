package com.sgtc.launcher.ClockSkin.Util;

import java.util.Calendar;

public class TimeUnit{
    public static int year;
    public static int month;
    public static int day;
    public static int dayOfWeek;
    public static int hour;
    public static int minute;
    public static int second;
    public static int millisecond;
    public static Calendar calendar;

    public static void tick(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        millisecond = calendar.get(Calendar.MILLISECOND);
    }

    public static int getActualMaximumDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        return a.get(Calendar.DATE);
    }

}
