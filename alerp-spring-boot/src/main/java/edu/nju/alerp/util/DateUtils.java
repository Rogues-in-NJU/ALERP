package edu.nju.alerp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 日期工具类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-23 19:25
 */
public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat sdfInDay = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 获取当前时间，精确到分钟
     * @return
     */
    public static String getTodayAccurateToMinute() {
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间，精确到天
     * @return
     */
    public static String getTodayAccurateToDay() {
        return sdfInDay.format(new Date());
    }

    /**
     * 获取当天是几号
     *
     * @return
     */
    public static int getNowDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取还款日
     */
    public static String getDueDate(int payDate) {
        Calendar calendar = Calendar.getInstance();
        int today = getNowDay();
        if (payDate <= today) {
            calendar.add(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, payDate);
        return sdfInDay.format(calendar.getTime());
    }

    /**
     * 获取日期差
     */
    public static long getTimeDifference(String date) {
        long day = 0;
        if (date == null) {
            return day;
        }

        try {
            Date createdAt = sdf.parse(date);
            Date now = new Date();
            long l = now.getTime() - createdAt.getTime();
            day = l / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }
}
