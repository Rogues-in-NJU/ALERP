package edu.nju.alerp.common;


import com.alibaba.fastjson.JSON;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 对未登陆的用户拒绝访问
 * @Author: yangguan
 * @CreateDate: 2020-01-05 21:29
 */
@Slf4j
//@WebFilter(filterName = "LoginFilter", urlPatterns = {"/api/*"})
public class LoginFilter implements Filter {

    String[] accessUri = {"/api/user/login"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        if (!HttpMethod.OPTIONS.matches(httpServletRequest.getMethod())
                && CommonUtils.getUserId() == 0
                && !"/api/user/login".equals(httpServletRequest.getRequestURI())) {
//            throw new NJUException(ExceptionEnum.ILLEGAL_USER, "请登陆后再访问该接口");
            responseWrapper.setStatus(200);
            responseWrapper.setHeader("Content-Type", "application/json;charset=UTF-8");

            String result = JSON.toJSONString(ResponseResult.fail(ExceptionEnum.ILLEGAL_USER, "请登陆后再访问该接口"));
            responseWrapper.getOutputStream().write(result.getBytes());
            responseWrapper.getOutputStream().flush();
        }else {
            filterChain.doFilter(httpServletRequest, servletResponse);
        }
    }

    private boolean ifAccess(String uri) {
        for (String access : accessUri) {
            if (uri.equals(access))
                return true;
        }
        return false;
    }
}
