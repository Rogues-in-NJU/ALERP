package edu.nju.alerp.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
    }

    public static HttpSession getHttpSession() {
        return getHttpServletRequest().getSession();
    }
}
