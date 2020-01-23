package edu.nju.alerp.common.auth;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.ResponseWrapper;
import edu.nju.alerp.enums.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 权限Filter
 * @Author: yangguan
 * @CreateDate: 2020-01-08 16:37
 */
@Slf4j
//@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        AuthContext authContext = AuthRegistry.getOrCreateAuthContext();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        authContext.setHttpServletRequest(request);

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        authContext.setHttpServletResponse(responseWrapper);

        try {
            filterChain.doFilter(request, responseWrapper);
        }catch (Exception e) {
            if (e.getCause() instanceof NJUException) {
                NJUException njuException = (NJUException) e.getCause();
                if (njuException.getExceptionEnum() == ExceptionEnum.AUTH_FAIL) {
                    responseWrapper.setStatus(200);
                    responseWrapper.setHeader("Content-Type", "application/json;charset=UTF-8");

                    String result = JSON.toJSONString(ResponseResult.fail(ExceptionEnum.AUTH_FAIL, njuException.getMessage()));
                    responseWrapper.getOutputStream().write(result.getBytes());
                    responseWrapper.getOutputStream().flush();
                }
            }
        }finally {
            AuthRegistry.clearAuthContext();
        }
    }
}
