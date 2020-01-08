package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.auth.AuthContext;
import edu.nju.alerp.common.auth.AuthParamsSupplier;
import edu.nju.alerp.common.auth.AuthRegistry;
import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.AuthDTO;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
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

    private Map<Integer, Auth> authCache = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Auth> auths = findAll();
        authCache.putAll(auths.parallelStream().map(auth -> MutablePair.of(auth.getId(), auth)).collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
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
    public int addOrUpdateAuth(AuthDTO authDTO) {
        Auth auth = Auth.builder().description(authDTO.getDescription())
                                .route(authDTO.getRoute()).build();

        if (authDTO.getId() != null) {
            auth = authRepository.getOne(auth.getId());
            auth.setDescription(authDTO.getDescription());
            auth.setRoute(authDTO.getRoute());
        }

        int res = authRepository.saveAndFlush(auth).getId();
        authCache.put(res, auth);
        return res;
    }

    @Override
    public Auth findAuthByUri(String uri) {
        for (Map.Entry<Integer, Auth> authEntry : authCache.entrySet()) {
            Auth auth = authEntry.getValue();
            if (Pattern.matches(auth.getRoute(), uri))
                return auth;
        }
        return null;
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
