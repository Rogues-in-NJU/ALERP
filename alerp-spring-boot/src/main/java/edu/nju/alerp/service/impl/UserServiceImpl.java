package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.LoginDTO;
import edu.nju.alerp.dto.LoginResultDTO;
import edu.nju.alerp.entity.*;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.LoginResult;
import edu.nju.alerp.repo.UserCityRelationRepository;
import edu.nju.alerp.service.AuthService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.enums.UserStatus;
import edu.nju.alerp.repo.UserRepository;
import edu.nju.alerp.dto.UserDTO;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.util.PasswordUtil;
import edu.nju.alerp.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用户服务层实现
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:41
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService, InitializingBean {


    @Autowired
    UserRepository userRepository;
    @Autowired
    UserCityRelationRepository userCityRelationRepository;
    @Autowired
    AuthService authService;

    @Resource
    private Cache<Integer, Object> userCache;

    private static String initPassword = "00000000";

    @Override
    public void afterPropertiesSet() throws Exception {
        List<User> users = userRepository.findAll();
        userCache.putAll(users.stream().map(u -> MutablePair.of(u.getId(), u)).collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
    }

    /**
     * 添加/保存用户，添加用户时初始密码为00000000,并对应赋予相应权限和城市
     *
     * @param userDTO
     * @return
     * @throws Exception
     */
    @Override
    public int saveUser(UserDTO userDTO) throws Exception {
        User res;
        if (userDTO.getId() == null) {
            User user = User.builder()
                    .name(userDTO.getName())
                    .password(PasswordUtil.getMD5(initPassword))
                    .updatedAt(DateUtils.getTodayAccurateToSecond())
                    .phoneNumber(userDTO.getPhoneNumber())
                    .createdAt(DateUtils.getTodayAccurateToSecond())
                    .status(UserStatus.ONJOB.getCode())
                    .build();
            res = userRepository.saveAndFlush(user);
            //新增用户初始化权限
            authService.initialUserAuthByUserId(res.getId());
            userDTO.getAuthList().forEach(a -> {
                a.setUserId(res.getId());
            });
            authService.updateUserAuth(userDTO.getAuthList());
        } else {
            User user = getUser(userDTO.getId());
            if (!userDTO.getUpdatedAt().equals(user.getUpdatedAt())) {
                throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "用户信息已变更，请重新更新");
            }
            user.setName(userDTO.getName());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setUpdatedAt(DateUtils.getTodayAccurateToSecond());
            res = userRepository.save(user);
            authService.updateUserAuth(userDTO.getAuthList());
        }
        List<Integer> cities = userCityRelationRepository.findCitiesByUserId(res.getId());
        userDTO.getCities().forEach(c -> {
            if (!cities.contains(c)) {
                UserCityRelation userCityRelation = UserCityRelation.builder()
                        .userId(res.getId())
                        .cityId(c)
                        .build();
                userCityRelationRepository.saveAndFlush(userCityRelation);
            }
        });


        userCache.put(res.getId(), res);
        return res.getId();
    }

    /**
     * 根据id获取用户
     *
     * @param id
     * @return
     */
    @Override
    public User getUser(int id) {
        User user = (User) userCache.get(id);
        if (user == null) {
            user = userRepository.getOne(id);
            if (user != null) {
                userCache.put(id, user);
            }
        }
        return user;
    }

    @Override
    public int reloadPassWord(int id) {
        User user = (User) userCache.get(id);
        user.setPassword(PasswordUtil.getMD5("00000000"));
        return userRepository.save(user).getId();
    }

    /**
     * 通过手机获取用户
     *
     * @param phoneNumber
     * @return
     */
    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findDistinctByPhoneNumber(phoneNumber);
    }

    /**
     * 根据id删除用户
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public int deleteUser(int id) throws Exception {
        User user = getUser(id);
        if (user == null) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "用户id不存在！");
        }
        if (user.getStatus() == UserStatus.OFFJOB.getCode()) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "删除用户失败，用户已被删除！");
        }
        user.setStatus(UserStatus.OFFJOB.getCode());
        user.setDeletedAt(DateUtils.getTodayAccurateToSecond());
        int ans = userRepository.save(user).getId();
        userCache.put(id, user);
        return ans;
    }

    /**
     * 分页查询用户列表
     *
     * @param pageable
     * @param name
     * @param status
     * @param userList
     * @return
     */
    @Override
    public Page<UserVO> getUserList(Pageable pageable, String name, Integer status, List<Integer> userList) {
        QueryContainer<User> sp = new QueryContainer<>();
        try {
            if (status != null) {
                sp.add(ConditionFactory.equal("status", status));
            }
            if (!CollectionUtils.isEmpty(userList)) {
                sp.add(ConditionFactory.In("id", userList));
            }
            List<Condition> fuzzyMatch = new ArrayList<>();
            if (!"".equals(name)) {
                fuzzyMatch.add(ConditionFactory.like("name", name));
                fuzzyMatch.add(ConditionFactory.like("phoneNumber", name));
            }
            if (!fuzzyMatch.isEmpty()) {
                sp.add(ConditionFactory.or(fuzzyMatch));
            }
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        Page<User> ans = sp.isEmpty() ? userRepository.findAll(pageable) : userRepository.findAll(sp, pageable);
        List<UserVO> userVOList = new ArrayList<>();
        ans.getContent().forEach(u -> {
            UserVO userVO = UserVO.builder().build();
            BeanUtils.copyProperties(u, userVO);
            userVOList.add(userVO);
        });
        return new PageImpl<>(userVOList, pageable, ans.getTotalElements());
    }

    /**
     * 用户登录，不同错误返回不同字段
     *
     * @param loginDTO
     * @return
     */
    @Override
    public LoginResultDTO checkLogin(LoginDTO loginDTO) {
        LoginResultDTO loginResultDTO;
        User user = getUserByPhoneNumber(loginDTO.getPhoneNumber());
        if (user == null) {
            loginResultDTO = LoginResultDTO.builder()
                    .code(LoginResult.NONE.getCode())
                    .result(LoginResult.NONE.getMessage())
                    .build();
            return loginResultDTO;
        }
        if (user.getStatus() != UserStatus.ONJOB.getCode()) {
            loginResultDTO = LoginResultDTO.builder()
                    .code(LoginResult.OFFJOB.getCode())
                    .result(LoginResult.OFFJOB.getMessage())
                    .build();
            return loginResultDTO;
        }
        List<Integer> cityList = getCitiesByUserId(user.getId());
        if (!cityList.contains(loginDTO.getCity())) {
            loginResultDTO = LoginResultDTO.builder()
                    .code(LoginResult.DENIED.getCode())
                    .result(LoginResult.DENIED.getMessage())
                    .build();
            return loginResultDTO;
        }
        boolean res = user.getPassword().equals(PasswordUtil.getMD5(loginDTO.getPassword()));
        if (res) {
            loginResultDTO = LoginResultDTO.builder()
                    .code(LoginResult.SUCCESS.getCode())
                    .result(LoginResult.SUCCESS.getMessage())
                    .userId(user.getId())
                    .build();
            return loginResultDTO;
        }
        loginResultDTO = LoginResultDTO.builder()
                .code(LoginResult.INCORRECT.getCode())
                .result(LoginResult.INCORRECT.getMessage())
                .build();
        return loginResultDTO;
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @Override
    public int updateUser(User user) {
        return userRepository.save(user).getId();
    }

    /**
     * 全量获取用户列表
     *
     * @return
     */
    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @Deprecated
    @Override
    public int getIdFromName(String name) {
        return userRepository.findDistinctByName(name).getId();
    }

    /**
     * 根据用户id获取对应城市列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<Integer> getCitiesByUserId(int userId) {
        List<Integer> cityList = userCityRelationRepository.findCitiesByUserId(userId);
        log.info("userId:{}, cityList:{}", userId, cityList);
        return cityList;
    }

    /**
     * 根据城市id获取对应用户列表
     *
     * @param cityId
     * @return
     */
    @Override
    public List<Integer> getUserListByCityId(int cityId) {
        return userCityRelationRepository.getUserListByCityId(cityId);
    }
}
