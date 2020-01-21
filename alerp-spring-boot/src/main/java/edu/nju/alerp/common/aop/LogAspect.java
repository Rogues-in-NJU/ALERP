package edu.nju.alerp.common.aop;

import com.alibaba.fastjson.JSON;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.entity.OperationLog;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.service.OperationLogService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 操作日志切面
 * @Author: qianen.yin
 * @CreateDate: 2019-12-25 21:31
 */
@Slf4j
@Order(value = 1)
@Aspect
@Component
public class LogAspect {
    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private UserService userService;
    private final static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);

    private static Set<String> set = new HashSet<>();

    @PostConstruct
    private void init() {
        set.add("/api/user");
        set.add("/api/supplier");
        set.add("/api/shipping-order");
        set.add("/api/purchase-order");
        set.add("/api/process-order");
        set.add("/api/expense");
        set.add("/api/customer");
        set.add("/api/product");
        set.add("/api/auth");
    }

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        int userId = CommonUtils.getUserId();
        log.info("[Controller-Log] time:", System.currentTimeMillis());
        String methodInvokeLog = buildMethodInvokeLog(joinPoint, userId);
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String name = methodSignature.getMethod().getAnnotation(RequestMapping.class).name();
        try {
            doLogOperate(userId, name, joinPoint);
            Object result = joinPoint.proceed();
//            log.info("{}=>{}", methodInvokeLog, JSON.toJSONString(result));
            return result;
        } catch (Throwable e) {
            log.error(String.format("%s => fail", methodInvokeLog), e);
            throw e;
        } finally {

        }
    }

//    private void logOperate(Integer userId, String name, ProceedingJoinPoint joinPoint) {
//        EXECUTOR_SERVICE.submit(() -> doLogOperate(userId, name, joinPoint));
//    }

    private void doLogOperate(Integer userId, String name, ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = CommonUtils.getHttpServletRequest();
        try {
            OperationLog operationLog = OperationLog.builder()
                    .createdAt(TimeUtil.dateFormat(new Date()))
                    .userId(userId)
                    .userName(userService.getUser(userId).getName())
                    .description(name)
                    .build();
            if (set.contains(request.getRequestURI())) {
                String[] names = name.split("/");
                if (names.length >= 2) {
                    boolean ans = check(joinPoint);
                    operationLog.setDescription(ans ? names[0] : names[1]);
                }
            }
            operationLogService.addOperationLog(operationLog);
        } catch (Exception e) {
            log.error("insertOperateLog fail", e);
        }
    }

    /**
     * 判断请求是添加还是更新，如果是添加返回true,否则返回false
     */
    private boolean check(ProceedingJoinPoint joinPoint) {
        Object[] o = joinPoint.getArgs();
        String fieldName = "id";
        if (o.length == 0) {
            throw new NJUException(ExceptionEnum.ILLEGAL_PARAM, "参数不正确!");
        }
        Class postClass = o[0].getClass();
        Field[] fields = postClass.getDeclaredFields();
        boolean flag = false;
        Object id = null;
        try {
            for (Field f : fields) {
                if (f.getName().equals(fieldName)) {
                    f.setAccessible(true);
                    id = f.get(o[0]);
                    flag = true;
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!flag) {
            throw new NJUException(ExceptionEnum.ILLEGAL_PARAM, "参数不正确!");
        }

        return id == null;
    }


    private String buildMethodInvokeLog(ProceedingJoinPoint joinPoint, int userid) {
        StringBuilder sb = new StringBuilder();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String method = joinPoint.getSignature().getName();
        sb.append(String.format("[Controller-Log][%s]=>", userid));//这边后面获取userid
        sb.append(className + "." + method + "(");
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i != args.length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
