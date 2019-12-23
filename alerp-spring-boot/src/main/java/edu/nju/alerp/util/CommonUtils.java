package edu.nju.alerp.util;

/**
 * @Description: 公有工具类
 * @Author: yangguan
 * @CreateDate: 2019-12-21 15:17
 */
public class CommonUtils {
    public static String fuzzyStringSplicing(String str) {
        String var = "%%%s%%";
        return String.format(var, str);
    }
}
