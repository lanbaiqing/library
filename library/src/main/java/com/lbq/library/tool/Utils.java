package com.lbq.library.tool;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.Formatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
    public static int dpToPx(Context context,int dp)
    {
        return (int)((dp * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
    public static int pxToDp(Context context,int px)
    {
        return (int)((px / context.getResources().getDisplayMetrics().density) + 0.5f);
    }
    public static int getWidthPx(Context context)
    {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public static int getHeightPx(Context context)
    {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    public static boolean areNotificationsEnabled(Context context)
    {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
    public static boolean isMobile(String mobile)
    {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }
    public static String formatSize(Context context,long size)
    {
        return  Formatter.formatFileSize(context, size);
    }
}
