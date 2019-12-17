package edu.nju.alerp.user.service.Impl;

import edu.nju.alerp.entity.User;
import edu.nju.alerp.enums.UserStatus;
import edu.nju.alerp.user.dto.UserDTO;
import edu.nju.alerp.user.service.UserService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: 用户服务层实现
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:41
 */
@Service
public class UserServiceImpl implements UserService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public boolean addUser(UserDTO userDTO) {
        User user = User.builder()
                .name(userDTO.getName())
                .password(userDTO.getPassword())
                .phone_number(userDTO.getPhone_number())
                .created_at(sdf.format(new Date()))
                .status(UserStatus.ONJOB.getCode())
                .build();

        //todo dao层add user
        return false;
    }

    @Override
    public boolean updateUser(UserDTO userDTO) {
        User user = getUser(userDTO.getId());
        if(user == null){
            return false;
        }
        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setPhone_number(userDTO.getPhone_number());
        user.setUpdated_at(sdf.format(new Date()));

        //todo dao层update user
        return true;
    }

    @Override
    public User getUser(int id) {
        return null;
    }

    @Override
    public boolean deleteUser(int id) {
        User user = getUser(id);
        if(user == null){
            return false;
        }
        user.setStatus(UserStatus.OFFJOB.getCode());
        user.setDeleted_at(sdf.format(new Date()));

        //todo dao层update user
        return true;
    }

    @Override
    public List<User> getUserList() {
        return null;
    }
}
