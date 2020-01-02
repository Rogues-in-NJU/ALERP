package edu.nju.alerp.util;

import org.springframework.util.DigestUtils;

/**
 * @Description: 作用描述
 * @Author: qianen.yin
 * @CreateDate: 2020-01-02 16:44
 */
public class PasswordUtil {
    private static final String slat = "&%5123***&&%%$$#@";

    /**
     * 生成md5
     *
     * @param
     * @return
     */
    public static String getMD5(String str) {
        String base = str + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
