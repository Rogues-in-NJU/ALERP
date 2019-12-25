package edu.nju.alerp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 日期工具类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-23 19:25
 */
public class DateUtils {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String getToday(){
        return sdf.format(new Date());
    }
}
