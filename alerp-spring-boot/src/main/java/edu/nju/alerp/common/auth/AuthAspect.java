package edu.nju.alerp.common.auth;


import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.entity.Auth;
import edu.nju.alerp.entity.AuthUser;
import edu.nju.alerp.enums.ActionType;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.service.AuthService;
import edu.nju.alerp.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 权限切面
 * @Author: yangguan
 * @CreateDate: 2020-01-08 16:52
 */
@Aspect
@Slf4j
public class AuthAspect {

    private AuthService authService;

    public AuthAspect(AuthService authService){
        this.authService = authService;
    }

    private static final String POINT_CUT = "execution(public * edu.nju.alerp..*Controller.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)";

    @Pointcut(POINT_CUT)
    public void pointCut() {

    }

//    @Pointcut(POINT_CUT)
    @Around(POINT_CUT)
    public Object doAroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        AuthContext authContext = AuthRegistry.getOrCreateAuthContext();
        authContext.setRequest(args);

        boolean verifyResult = true;
        try {
            verifyResult = verify(authContext);
        }catch (Exception e) {
            log.warn("Error verifying auth, {}", authContext, e);
            throw new NJUException(ExceptionEnum.AUTH_FAIL, "No Auth");
        }

        if (verifyResult) {
            Object result = pjp.proceed(args);
            return result;
        }

        throw new NJUException(ExceptionEnum.AUTH_FAIL, "No Auth");
    }

    private boolean verify(AuthContext authContext) throws Exception{
        HttpServletRequest servletRequest = authContext.getHttpServletRequest();
        AuthRegistry.AuthInfo authInfos = AuthRegistry.getAuthInfo(servletRequest);
        if (authInfos == null || authInfos.getVerifySupplier() == null)
            return true;

        AuthParamsSupplier authParamsSupplier = authInfos.getVerifySupplier();
        authParamsSupplier.consume(authContext);

        int userId = CommonUtils.getUserId();
        Auth auth = authService.findAuthByUri(servletRequest.getRequestURI());
        if (auth == null)
            return false;

        int authId = auth.getId();
        AuthUser authUser = authService.findAuthUser(userId, authId);
        return ActionType.of(authUser.getAction()) == ActionType.ACCESS;
    }
}
