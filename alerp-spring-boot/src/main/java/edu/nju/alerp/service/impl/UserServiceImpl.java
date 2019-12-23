package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.enums.UserStatus;
import edu.nju.alerp.repo.UserRepository;
import edu.nju.alerp.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 用户服务层实现
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:41
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    @Override
    public int saveUser(UserDTO userDTO) {
        User user = null;
        if(userDTO.getId() == null){
            user = User.builder()
                    .name(userDTO.getName())
                    .password(userDTO.getPassword())
                    .phone_number(userDTO.getPhone_number())
                    .created_at(sdf.format(new Date()))
                    .status(UserStatus.ONJOB.getCode())
                    .build();
        }
        else{
            user = getUser(userDTO.getId());
            user.setName(userDTO.getName());
            user.setPassword(userDTO.getPassword());
            user.setPhone_number(userDTO.getPhone_number());
            user.setUpdated_at(sdf.format(new Date()));
        }
        return userRepository.saveAndFlush(user).getId();
    }

    @Override
    public User getUser(int id) {
        return userRepository.getOne(id);
    }

    @Override
    public boolean deleteUser(int id) {
        User user = getUser(id);
        if (user == null) {
            return false;
        }
        user.setStatus(UserStatus.OFFJOB.getCode());
        user.setDeleted_at(sdf.format(new Date()));
        userRepository.save(user);
        return true;
    }

    @Override
    public Page<User> getUserList(Pageable pageable, String name, int status) {
        QueryContainer<Product> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("status", status));
            List<Condition> fuzzyMatch = new ArrayList<>();
            fuzzyMatch.add(ConditionFactory.like("name", name));
            fuzzyMatch.add(ConditionFactory.like("shorthand", name));
            fuzzyMatch.add(ConditionFactory.like("phone_number", name));
            sp.add(ConditionFactory.or(fuzzyMatch));
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        return userRepository.findAll(sp, pageable);
    }

    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }
}
