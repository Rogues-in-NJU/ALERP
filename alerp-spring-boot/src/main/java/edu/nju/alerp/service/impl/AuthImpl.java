package edu.nju.alerp.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.nju.alerp.common.auth.AuthContext;
import edu.nju.alerp.common.auth.AuthParamsSupplier;
import edu.nju.alerp.common.auth.AuthRegistry;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.AuthDTO;
import edu.nju.alerp.dto.UpdateUserAuthDTO;
import edu.nju.alerp.entity.Auth;
import edu.nju.alerp.entity.AuthUser;
import edu.nju.alerp.enums.AuthClassification;
import edu.nju.alerp.repo.AuthRepository;
import edu.nju.alerp.repo.AuthUserRepository;
import edu.nju.alerp.service.AuthService;
import edu.nju.alerp.vo.AuthUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
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
@EnableScheduling
public class AuthImpl implements AuthService, InitializingBean {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    private Map<Integer, Auth> authCache = new ConcurrentHashMap<>();

    private Map<AuthUserKey, AuthUser> authUserCache = new ConcurrentHashMap<>();

    private static final Set<String> managerRoute = ImmutableSet.of("/api/product/delete/([1-9]\\d*)?",
                                                            "/api/product/list");
    private static final List<MutablePair> initialAuthResource =
            ImmutableList.of(MutablePair.of("/api/product/list", "查看商品列表"),
                    MutablePair.of("/api/product", "新增或者编辑商品"),
                    MutablePair.of("/api/product/([1-9]\\d*)?", "查看商品详情"),
                    MutablePair.of("/api/product/delete/([1-9]\\d*)?", "废弃商品"),
                    // 收款单
                    MutablePair.of("/api/arrear-order/([1-9]\\d*)?", "获取欠款明细详情"),
                    MutablePair.of("/api/arrear-order/due-date", "修改欠款明细截止日期"),
                    MutablePair.of("/api/arrear-order/invoice-number", "修改发票流水号"),
                    MutablePair.of("/api/arrear-order/list", "查询欠款明细列表"),
                    MutablePair.of("/api/overdue-warning", "获取欠款统计"),
                    // 客户
                    MutablePair.of("/api/customer/([1-9]\\d*)?", "查看客户详细信息"),
                    MutablePair.of("/api/customer/delete/([1-9]\\d*)?", "删除客户"),
                    MutablePair.of("/api/customer/list", "获取客户列表"),
                    MutablePair.of("/api/customer", "新增客户/修改客户信息"),
                    // 公司支出
                    MutablePair.of("/api/expense", "新增公司支出"),
                    MutablePair.of("/api/expense/delete/([1-9]\\d*)?", "删除公司支出"),
                    MutablePair.of("/api/expense/list", "获取公司支出列表"),
                    // 加工单
                    MutablePair.of("/api/process-order/list", "获取加工单列表"),
                    MutablePair.of("/api/process-order/([1-9]\\d*)?", "查询加工单详情"),
                    MutablePair.of("/api/process-order", "新增或者更新加工单"),
                    MutablePair.of("/api/process-order/product", "新增或者更新加工单商品"),
                    MutablePair.of("/api/process-order/product/delete/([1-9]\\d*)?", "删除加工单商品"),
                    MutablePair.of("/api/process-order/print/([1-9]\\d*)?", "打印加工单"),
                    MutablePair.of("/api/process-order/abandon/([1-9]\\d*)?", "废弃加工单"),
                    // 采购单
                    MutablePair.of("/api/purchase-order/list", "获取采购单列表"),
                    MutablePair.of("/api/purchase-order/([1-9]\\d*)?", "获取采购单详情"),
                    MutablePair.of("/api/purchase-order", "新增采购单"),
                    MutablePair.of("/api/purchase-order/payment-record", "新增采购单付款记录"),
                    MutablePair.of("/api/purchase-order/payment-record/delete/([1-9]\\d*)?", "废弃采购单付款记录"),
                    MutablePair.of("/api/purchase-order/abandon/([1-9]\\d*)?", "废弃采购单"),
                    // 收款记录
                    MutablePair.of("/api/arrear-order/receipt-record/", "增加收款记录"),
                    MutablePair.of("/api/arrear-order/receipt-record/delete/([1-9]\\d*)?", "删除收款记录"),
                    // 出货单
                    MutablePair.of("/api/shipping-order/delete/([1-9]\\d*)?", "删除出货单"),
                    MutablePair.of("/api/shipping-order/list", "获取出货单列表"),
                    MutablePair.of("/api/shipping-order", "新增出货单"),
                    MutablePair.of("/api/shipping-order/([1-9]\\d*)?", "查看出货单详情"),
                    MutablePair.of("/api/shipping-order/productAvgPriceslist", "获取商品平均单价列表"),
                    // 汇总信息
                    MutablePair.of("/api/summary/info", "获取汇总信息"),
                    MutablePair.of("/api/summary/product", "获取商品汇总信息"),
                    // 供货商
                    MutablePair.of("/api/supplier/list", "获取供货商列表"),
                    MutablePair.of("/api/supplier", "新增或者更新供货商"),
                    MutablePair.of("/api/supplier/delete/([1-9]\\d*)?", "删除供货商"),
                    // 用户
                    MutablePair.of("/api/user/delete/([1-9]\\d*)?", "删除用户"),
                    MutablePair.of("/api/user/list", "获取用户列表"),
                    MutablePair.of("/api/user/([1-9]\\d*)?", "获取用户详细信息"),
                    MutablePair.of("/api/user/updatePassword", "修改当前用户密码"),
                    MutablePair.of("/api/user/self", "获取登录用户详细信息"),
                    MutablePair.of("/api/user/operation-log/list", "用户操作日志查询"),
                    MutablePair.of("/api/user", "新增用户/修改用户信息"),
//                    MutablePair.of("/api/user/login", "用户登录"),
//                    MutablePair.of("/api/user/logout", "用户登出")
                    // 权限
                    MutablePair.of("/api/auth/list", "查询权限资源列表"),
                    MutablePair.of("/api/auth/edit", "编辑用户权限"),
                    MutablePair.of("/api/userId/([1-9]\\d*)?", "查询用户的拥有权限")
            );

    private static final int initailDelayTime = 1000 * 30;
    private static final int fixedDelayTime = 1000 * 30;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Auth> auths = findAll();
        authCache.putAll(auths.parallelStream().map(auth -> MutablePair.of(auth.getId(), auth)).collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
        loadAuthUserCache();
        auths.forEach(auth -> AuthRegistry.register(auth.getRoute(), new AuthParamsSupplier() {
            @Override
            public void consume(AuthContext authContext) {
                // do something
            }
        }));
    }

    @Scheduled(initialDelay = initailDelayTime, fixedDelay = fixedDelayTime)
    private void refreshAuthUserCache() {
        loadAuthUserCache();
    }

    private void loadAuthUserCache() {
        List<AuthUser> authUsers = authUserRepository.findAll();
        authUserCache.putAll(authUsers.parallelStream().map(authUser -> MutablePair.of(new AuthUserKey(authUser.getUserId(), authUser.getAuthId()), authUser))
                .collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
    }

    @Override
    public List<Auth> findAll() {
        return authRepository.findAll(Sort.by(Sort.Direction.ASC, "route"));
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
            if (authUser != null) {
                if (authUser.getAction() != us.getAction()) {
                    authUser.setAction(us.getAction());
                    authUsers.add(authUser);
                }
            }else {
                authUsers.add(AuthUser.builder().userId(us.getUserId())
                                                .authId(us.getAuthId())
                                                .action(us.getAction()).build());
            }
        }

        authUserRepository.saveAll(authUsers);
        authUserRepository.flush();
        return 0;
    }

    @Override
    public int initialUserAuthByUserId(int id) {
        List<AuthUser> authUsers = authCache.values().parallelStream()
                                        .map(auth -> {
                                            AuthUser authUser = AuthUser.builder()
                                                    .userId(id)
                                                    .authId(auth.getId()).build();
//                                            if (managerRoute.contains(auth.getRoute()))
                                                authUser.setAction(1);
//                                            else
//                                                authUser.setAction(0);
                                            return authUser;
                                        })
                                        .collect(Collectors.toList());

        authUserRepository.saveAll(authUsers);
        authUserRepository.flush();
        return 0;
    }

    @Override
    public Map<Integer, List<AuthUserVO>> queryAuthUserByUserId(int userId) {
        QueryContainer<AuthUser> authUserSp = new QueryContainer<>();
        try {
            authUserSp.add(ConditionFactory.equal("userId", userId));
        }catch (Exception e) {
            log.error("error", e);
        }
        List<AuthUser> authUsers = authUserRepository.findAll(authUserSp);
        List<AuthUserVO> result = authUsers.parallelStream()
                                            .map(authUser -> {
                                                AuthUserVO authUserVO = new AuthUserVO();
                                                authUserVO.setId(authUser.getId());
                                                authUserVO.setAuthId(authUser.getAuthId());
                                                authUserVO.setDescription(authCache.get(authUser.getAuthId()).getDescription());
                                                authUserVO.setUserId(authUser.getUserId());
                                                authUserVO.setAction(authUser.getAction());
                                                return authUserVO;
                                            }).collect(Collectors.toList());

        Map<Integer, List<AuthUserVO>> resultMap = new HashMap<>();
        for (AuthUserVO authUserVO : result) {
            try {
                int k = AuthClassification.getClassificationFromAuthId(authUserVO.getAuthId()).getCode();
                List<AuthUserVO> temp = resultMap.get(k);
                if (temp == null) {
                    temp = new ArrayList<>();
                    temp.add(authUserVO);
                    resultMap.put(k, temp);
                }else {
                    temp.add(authUserVO);
                }
            }catch (Exception e) {
                log.error(authUserVO.getDescription()+"can not find,",e);
            }

        }
        return resultMap;
    }

    @Override
    public int initialAuthResource() {
        List<Auth> auths = initialAuthResource.stream()
                                            .map(mutablePair -> Auth.builder().route((String) mutablePair.getLeft())
                                                                                .description((String) mutablePair.getRight()).build())
                                            .collect(Collectors.toList());
        authRepository.saveAll(auths);
        authRepository.flush();
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
        if (authUsers.size() == 0)
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
