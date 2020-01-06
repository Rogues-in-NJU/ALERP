package edu.nju.alerp.common;


import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Description: 对未登陆的用户拒绝访问
 * @Author: yangguan
 * @CreateDate: 2020-01-05 21:29
 */
@Slf4j
@WebFilter(filterName = "LoginFilter", urlPatterns = {"/api/*"})
public class LoginFilter implements Filter {

    String[] accessUri = {"/api/user/login"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (CommonUtils.getUserId() == 0 && !ifAccess(httpServletRequest.getRequestURI())) {
            throw new NJUException(ExceptionEnum.ILLEGAL_USER, "请登陆后再访问该接口");
        }
        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    private boolean ifAccess(String uri) {
        for (String access : accessUri) {
            if (uri.equals(access))
                return true;
        }
        return false;
    }
}
