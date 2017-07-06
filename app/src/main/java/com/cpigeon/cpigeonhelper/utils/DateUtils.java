package com.cpigeon.cpigeonhelper.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/7/6.
 */

public class DateUtils {

    private static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static String millis2String(final long millis) {
        return millis2String(millis, DEFAULT_FORMAT);
    }

    public static String millis2String(final long millis, final DateFormat format) {
        return format.format(new Date(millis));
    }

    public static String compareDate(String endTime, String nowTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = null;
        try {
            now = df.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date= null;
        try {
            date = df.parse(nowTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l=now.getTime()-date.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        long s=(l/1000-day*24*60*60-hour*60*60-min*60);
        return ""+day+"天";
    }
}
