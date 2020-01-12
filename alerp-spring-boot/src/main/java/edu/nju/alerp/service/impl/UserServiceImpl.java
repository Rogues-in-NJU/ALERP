package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.LoginDTO;
import edu.nju.alerp.dto.LoginResultDTO;
import edu.nju.alerp.entity.OperationLog;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.entity.UserCityRelation;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.LoginResult;
import edu.nju.alerp.repo.UserCityRelationRepository;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.enums.UserStatus;
import edu.nju.alerp.repo.UserRepository;
import edu.nju.alerp.dto.UserDTO;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.util.PasswordUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Resource
    private Cache<Integer, Object> userCache;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<User> users = userRepository.findAll();
        userCache.putAll(users.stream().map(u -> MutablePair.of(u.getId(), u)).collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight)));
    }

    @Override
    public int saveUser(UserDTO userDTO) {
        User user;
        if (userDTO.getId() == null) {
            user = User.builder()
                    .name(userDTO.getName())
                    .password(PasswordUtil.getMD5(userDTO.getPassword()))
                    .updatedAt(DateUtils.getToday())
                    .phoneNumber(userDTO.getPhoneNumber())
                    .createdAt(DateUtils.getToday())
                    .status(UserStatus.ONJOB.getCode())
                    .build();
        } else {
            user = getUser(userDTO.getId());
            if (!userDTO.getUpdateAt().equals(user.getUpdatedAt())) {
                throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "用户信息已变更，请重新更新");
            }
            user.setName(userDTO.getName());
            user.setPassword(PasswordUtil.getMD5(userDTO.getPassword()));
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setUpdatedAt(DateUtils.getToday());
        }
        User res = userRepository.saveAndFlush(user);
        List<Integer> cities = userCityRelationRepository.findCitiesByUserId(res.getId());
        userDTO.getCity().forEach(c -> {
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
    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findDistinctByPhoneNumber(phoneNumber);
    }

    @Override
    public int deleteUser(int id) {
        User user = getUser(id);
        if (user == null) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "用户id不存在！");
        }
        if (user.getStatus() == UserStatus.OFFJOB.getCode()) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "删除用户失败，用户已被删除！");
        }
        user.setStatus(UserStatus.OFFJOB.getCode());
        user.setDeletedAt(DateUtils.getToday());
        int ans = userRepository.save(user).getId();
        userCache.put(id, user);
        return ans;
    }

    @Override
    public Page<User> getUserList(Pageable pageable, String name, int status) {
        QueryContainer<User> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("status", status));
            sp.add(ConditionFactory.equal("city", CommonUtils.getCity()));
            List<Condition> fuzzyMatch = new ArrayList<>();
            if (!"".equals(name)) {
                fuzzyMatch.add(ConditionFactory.like("name", name));
                fuzzyMatch.add(ConditionFactory.like("phoneNumber", name));
            }
            sp.add(ConditionFactory.or(fuzzyMatch));
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        return sp.isEmpty() ? userRepository.findAll(pageable) : userRepository.findAll(sp, pageable);
    }

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

    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @Override
    public int getIdFromName(String name) {
        return userRepository.findDistinctByName(name).getId();
    }

    @Override
    public List<Integer> getCitiesByUserId(int userId) {
        List<Integer> cityList = userCityRelationRepository.findCitiesByUserId(userId);
        log.info("userId:{}, cityList:{}", userId, cityList);
        return cityList;
    }
}
