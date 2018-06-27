package com.lbq.library.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time
{
    public static final String yyyyMMddHHmmss1 = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
    public static final String yyyyMMdd = "yyyy-MM-dd";
    public static final String yyyyMMddHHmmss2 = "yyyy/MM/dd HH:mm:ss";
    public static final String MMddHHmm = "MM/dd HH:mm";
    public static final String HHmm = "HH:mm";
    public static String getTime(String type,long time)
    {
        return new SimpleDateFormat(type).format(new Date(time));
    }
    public static String getTime(String type,long time,int defValue)
    {
        return new SimpleDateFormat(type).format(new Date((time * defValue)));
    }
    public static String getMediaDuration(long T)
    {
        long hour   = T  / 1000 / (3600);
        long minute = (T / 1000 / 60) %60;
        long second = (T / 1000)  %60;
        return (hour == 0 ? "" : hour > 9 ? hour + ":" : "0" + hour + ":") + (minute == 0 ? "00:" : minute > 9 ? minute + ":" : "0" + minute + ":") + (second > 9 ? "" + second : "0" + second);
    }
    public static String getMediaDuration(long T,String H,String M,String S)
    {
        long hour = T/(60*60*1000);
        long minute = (T - hour*60*60*1000)/(60*1000);
        long second = (T - hour*60*60*1000 - minute*60*1000)/1000;
        return (hour == 0 ? "" : hour + H) + (minute == 0 ? "" : minute + M) + (second == 0 ? "" : second + S);
    }
}
