package edu.nju.alerp.common.auth;


import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 权限验证上下文类
 * @Author: yangguan
 * @CreateDate: 2019-12-27 11:55
 */
@Data
public class AuthContext {

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private Object[] request;
    private Map<String, Object> verifyParams = new ConcurrentHashMap<>();
}
