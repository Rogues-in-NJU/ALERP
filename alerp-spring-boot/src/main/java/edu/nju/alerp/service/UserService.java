package edu.nju.alerp.service;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.dto.UserDTO;

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

    ListResponse getUserList(int pageIndex, int pageSize, String name, int status);

}
