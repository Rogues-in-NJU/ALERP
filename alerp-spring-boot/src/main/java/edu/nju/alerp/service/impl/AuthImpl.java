package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.auth.AuthContext;
import edu.nju.alerp.common.auth.AuthParamsSupplier;
import edu.nju.alerp.common.auth.AuthRegistry;
import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.entity.Auth;
import edu.nju.alerp.entity.AuthUser;
import edu.nju.alerp.repo.AuthRepository;
import edu.nju.alerp.repo.AuthUserRepository;
import edu.nju.alerp.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 权限资源服务实现类
 * @Author: yangguan
 * @CreateDate: 2020-01-08 17:14
 */
@Service
@Slf4j
public class AuthImpl implements AuthService, InitializingBean {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Resource
    private Cache<String, Object> authCache;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Auth> auths = findAll();
        authCache.putAll(auths.parallelStream().map(auth -> MutablePair.of(auth.getRoute(), auth)).collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
        auths.forEach(auth -> AuthRegistry.register(auth.getRoute(), new AuthParamsSupplier() {
            @Override
            public void consume(AuthContext authContext) {
                // do something
            }
        }));
    }

    @Override
    public List<Auth> findAll() {
        return authRepository.findAll();
    }

    @Override
    public int addOrUpdateAuth() {
        return 0;
    }

    @Override
    public Auth findAuthByUri(String uri) {
        Auth auth = (Auth) authCache.get(uri);
        if (auth == null) {
            auth = findAuthByUriWithSql(uri);
            if (auth != null) {
                authCache.put(auth.getRoute(), auth);
            }
        }
        return auth;
    }

    @Override
    public boolean findAuthUser(int userId, int authId) {
        QueryContainer<AuthUser> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("userId", userId));
            sp.add(ConditionFactory.equal("authId", authId));
        }catch (Exception e) {
            log.error("error", e);
        }
        List<AuthUser> authUsers = authUserRepository.findAll(sp);
        if ( authUsers != null && authUsers.size() >0 ) {
            return true;
        }
        return false;
    }

    private Auth findAuthByUriWithSql(String uri) {
        QueryContainer<Auth> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("route", uri));
        }catch (Exception e) {
            log.error("error", e);
        }
        List<Auth> auths = authRepository.findAll(sp);
        if (auths == null)
            return null;

        return auths.get(0);
    }
}
