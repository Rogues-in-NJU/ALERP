package edu.nju.alerp.util;

import org.springframework.util.StringUtils;
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

    public static int getUserId() {
        HttpSession session = getHttpSession();
        return session.getAttribute("userId") == null ? 0 : (int) session.getAttribute("userId");
    }

    public static int getCity() {
        HttpSession session = getHttpSession();
        return session.getAttribute("cityId") == null ? 0 : (int) session.getAttribute("cityId");
    }

    public static double processSpecification(String specification) {
        if (StringUtils.isEmpty(specification)) {
            return 0;
        }
        if (specification.charAt(0) == 'Φ') {
            specification = specification.substring(1);
        }
        String[] sp = specification.split("\\*");
        if (sp.length > 0 && sp[0].matches("^[0-9]+(.[0-9]+)?$")) {
            return Double.valueOf(sp[0]);
        }
        return 0;
    }
}
