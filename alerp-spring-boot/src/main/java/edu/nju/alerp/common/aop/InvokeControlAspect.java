package edu.nju.alerp.common.aop;

import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 环切，目前主要是业务异常和系统异常处理
 *
 * @author luhailong
 * @date 2019/12/29
 */
@Aspect
@Component
@Slf4j
public class InvokeControlAspect {

    /**
     * 环切，目前主要是业务异常和系统异常处理
     * @param joinPoint
     * @return
     */
    @Around("@annotation(InvokeControl)")
    public Object invokeHandle(ProceedingJoinPoint joinPoint) {
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            if (t instanceof NJUException) {
                // 业务异常，输入业务日志
                return ResponseResult.fail(((NJUException)t).getExceptionEnum(), t.getMessage());
            }
            // 系统异常，输出堆栈信息
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(t));
        }
    }
}
