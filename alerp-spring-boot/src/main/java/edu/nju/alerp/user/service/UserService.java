package edu.nju.alerp.user.service;

import edu.nju.alerp.entity.User;
import edu.nju.alerp.user.dto.UserDTO;

import java.util.List;

/**
 * @Description: 用户服务层接口
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:41
 */
public interface UserService {

    boolean addUser(UserDTO userDTO);

    boolean updateUser(UserDTO userDTO);

    User getUser(int id);

    boolean deleteUser(int id);

    List<User> getUserList();

}
