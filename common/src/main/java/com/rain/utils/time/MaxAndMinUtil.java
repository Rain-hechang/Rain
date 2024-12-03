package com.rain.utils.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: rain-he
 * @CreateTime: 2024-12-03
 * @Description: 获取指定日期的最小时间和最大时间
 * @Version: 1.0
 */

public class MaxAndMinUtil {
    public static String getStartOfDay(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }
    public static String getEndOfDay(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    public static String getStartTime(String beginTime){
        return beginTime + " 00:00:00";
    }

    public static String getEndTime(String endTime){
        return endTime + " 23:59:59";
    }
}
