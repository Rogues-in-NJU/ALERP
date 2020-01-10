package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.auth.AuthContext;
import edu.nju.alerp.common.auth.AuthParamsSupplier;
import edu.nju.alerp.common.auth.AuthRegistry;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.AuthDTO;
import edu.nju.alerp.dto.UpdateUserAuthDTO;
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

import java.util.ArrayList;
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

    private Map<AuthUserKey, AuthUser> authUserCache = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Auth> auths = findAll();
        List<AuthUser> authUsers = authUserRepository.findAll();
        authCache.putAll(auths.parallelStream().map(auth -> MutablePair.of(auth.getId(), auth)).collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
        authUserCache.putAll(authUsers.parallelStream().map(authUser -> MutablePair.of(new AuthUserKey(authUser.getUserId(), authUser.getAuthId()), authUser))
        .collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
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
    public AuthUser findAuthUser(int userId, int authId) {
        AuthUserKey authUserKey = new AuthUserKey(userId, authId);
        AuthUser authUser = authUserCache.get(authUserKey);
        if (authUser == null) {
            authUser = findAuthUserFromSql(userId, authId);
            if (authUser != null) {
                authUserCache.put(authUserKey, authUser);
                return authUser;
            }
            return null;
        }
        return authUser;
    }

    @Override
    public int updateUserAuth(List<UpdateUserAuthDTO> updateAuths) {
        List<AuthUser> authUsers = new ArrayList<>();
        for (UpdateUserAuthDTO us : updateAuths) {
            AuthUser authUser = findAuthUser(us.getUserId(), us.getAuthId());
            if (authUser != null && authUser.getAction() != us.getActionType()) {
                authUser.setAction(us.getActionType());
                authUsers.add(authUser);
            }else {
                log.error(us.getUserId()+"_"+us.getAuthId()+"can not find.");
            }
        }

        authUserRepository.saveAll(authUsers);
        authUserRepository.flush();
        return 0;
    }

    private AuthUser findAuthUserFromSql(int userId, int authId) {
        QueryContainer<AuthUser> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("userId", userId));
            sp.add(ConditionFactory.equal("authId", authId));
        }catch (Exception e) {
            log.error("error", e);
        }
        List<AuthUser> authUsers = authUserRepository.findAll(sp);
        if (authUsers == null)
            return null;
        if (authUsers.get(0) == null)
            return null;
        return authUsers.get(0);
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

    class AuthUserKey {
        private String userId;
        private String authId;

        public AuthUserKey(int userId, int authId) {
            this.userId = String.valueOf(userId).intern();
            this.authId = String.valueOf(authId).intern();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;

            if (! (obj instanceof AuthUserKey) )
                return false;

            AuthUserKey other = (AuthUserKey) obj;
            return (userId.equals(other.userId) && authId.equals(other.authId));
        }

        @Override
        public int hashCode() {
            return userId.hashCode() >>> 4 + authId.hashCode();
        }
    }
}
