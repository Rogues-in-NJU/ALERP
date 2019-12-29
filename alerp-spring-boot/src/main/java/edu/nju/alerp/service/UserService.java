package edu.nju.alerp.service;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.OperationLog;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description: 用户服务层接口
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:41
 */
public interface UserService {

    int saveUser(UserDTO userDTO);

    User getUser(int id);

    User getUserByPhoneNumber(String phoneNumber);

    boolean deleteUser(int id);

    List<User> getUserList();

    Page<User> getUserList(Pageable pageable, String name, int status);

    /**
     * 通过用户的姓名获取id
     * @param name
     * @return
     */
    int getIdFromName(String name);
}
