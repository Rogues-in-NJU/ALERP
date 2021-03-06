package edu.nju.alerp.service;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.dto.LoginDTO;
import edu.nju.alerp.dto.LoginResultDTO;
import edu.nju.alerp.entity.OperationLog;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.dto.UserDTO;
import edu.nju.alerp.entity.UserCityRelation;
import edu.nju.alerp.vo.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description: 用户服务层接口
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:41
 */
public interface UserService {

    int saveUser(UserDTO userDTO) throws Exception;

    User getUser(int id);

    int reloadPassWord(int id);

    User getUserByPhoneNumber(String phoneNumber);

    int deleteUser(int id) throws Exception;

    List<User> getUserList();

    Page<UserVO> getUserList(Pageable pageable, String name, Integer status, List<Integer> userList);

    /**
     * 通过用户的姓名获取id
     *
     * @param name
     * @return
     */
    int getIdFromName(String name);

    List<Integer> getCitiesByUserId(int userId);

    List<Integer> getUserListByCityId(int cityId);

    LoginResultDTO checkLogin(LoginDTO loginDTO);

    int updateUser(User user);

}
