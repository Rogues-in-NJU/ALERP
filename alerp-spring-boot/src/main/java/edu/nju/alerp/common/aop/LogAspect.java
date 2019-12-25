//package edu.nju.alerp.common.aop;
//
//import com.alibaba.fastjson.JSON;
//import edu.nju.alerp.entity.OperationLog;
//import edu.nju.alerp.service.OperationLogService;
//import edu.nju.alerp.service.UserService;
//import edu.nju.alerp.util.TimeUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.util.Date;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * @Description: 操作日志切面
// * @Author: qianen.yin
// * @CreateDate: 2019-12-25 21:31
// */
//@Slf4j
//@Order(value = 1)
//@Aspect
//@Component
//public class LogAspect {
//    @Autowired
//    private OperationLogService operationLogService;
//    @Autowired
//    private UserService userService;
//    private final static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);
//
//    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        HttpServletRequest request = getHttpServletRequest();
//        HttpSession session = request.getSession();
//        Integer userId = (Integer) session.getAttribute("userId");
//        log.info("[Controller-Log] time:", System.currentTimeMillis());
//        String methodInvokeLog = buildMethodInvokeLog(joinPoint, userId);
//        try {
//            logOperate(request, userId);
//            Object result = joinPoint.proceed();
//            log.info("{}=>{}", methodInvokeLog, JSON.toJSONString(result));
//            return result;
//        } catch (Throwable e) {
//            log.error(String.format("%s => fail", methodInvokeLog), e);
//            throw e;
//        } finally {
//
//        }
//    }
//
//    private void logOperate(HttpServletRequest request, Integer userId) {
//        EXECUTOR_SERVICE.submit(() -> doLogOperate(request, userId));
//    }
//
//    private void doLogOperate(HttpServletRequest request, Integer userId) {
//        try {
//            String description = request.getRequestURL().toString() + " Method:" + request.getMethod();
//            OperationLog operationLog = OperationLog.builder()
//                    .createdAt(TimeUtil.dateFormat(new Date()))
//                    .userId(userId)
//                    .userName(userService.getUser(userId).getName())
//                    .description(description)
//                    .build();
//            operationLogService.addOperationLog(operationLog);
//        } catch (Exception e) {
//            log.error("insertOperateLog fail", e);
//        }
//    }
//
//    private static HttpServletRequest getHttpServletRequest() {
//        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//                .getRequest();
//    }
//
//    private String buildMethodInvokeLog(ProceedingJoinPoint joinPoint, int userid) {
//        StringBuilder sb = new StringBuilder();
//        String className = joinPoint.getTarget().getClass().getSimpleName();
//        String method = joinPoint.getSignature().getName();
//        sb.append(String.format("[Controller-Log][%s]=>", userid));//这边后面获取userid
//        sb.append(className + "." + method + "(");
//        Object[] args = joinPoint.getArgs();
//        for (int i = 0; i < args.length; i++) {
//            sb.append(args[i]);
//            if (i != args.length - 1) {
//                sb.append(",");
//            }
//        }
//        sb.append(")");
//        return sb.toString();
//    }
//}
