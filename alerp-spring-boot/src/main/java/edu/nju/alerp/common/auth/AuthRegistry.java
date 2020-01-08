package edu.nju.alerp.common.auth;

import edu.nju.alerp.enums.AuthType;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 权限注册类
 * @Author: yangguan
 * @CreateDate: 2019-12-27 11:55
 */
public class AuthRegistry {

    @Data
    @AllArgsConstructor
    public static class AuthInfo {
        private AuthParamsSupplier verifySupplier;
        private AuthType authType;
    }

    private final static Map<String, AuthInfo> authInfos = new ConcurrentHashMap<>();
    private final static ThreadLocal<AuthContext> context = new ThreadLocal<>();

    public static void register(String uri, AuthParamsSupplier verifySupplier, AuthType authType) {
        AuthInfo authInfo = new AuthInfo(verifySupplier, authType);
        authInfos.put(uri, authInfo);
    }

    public static void register(String uri, AuthParamsSupplier verifySupplier) {
        AuthInfo authInfo = new AuthInfo(verifySupplier, null);
        authInfos.put(uri, authInfo);
    }

    public static AuthInfo getAuthInfo(HttpServletRequest httpServletRequest) {
        String uri = httpServletRequest.getRequestURI();
        AuthInfo authInfo = authInfos.get(uri);
        if (authInfo != null)
            return authInfo;
        return null;
    }

    public static AuthContext getOrCreateAuthContext() {
        if (context.get() == null) {
            synchronized (context) {
                if (context.get() == null) {
                    AuthContext authContext = new AuthContext();
                    context.set(authContext);
                }
            }
        }
        return context.get();
    }

    public static void clearAuthContext() {
        context.set(null);
    }
}
