package edu.nju.alerp.Service;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.enums.UserStatus;
import edu.nju.alerp.Repo.UserRepository;
import edu.nju.alerp.Dto.UserDTO;
import edu.nju.alerp.util.ListResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用户服务层实现
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:41
 */
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private ListResponseUtils listResponseUtils = new ListResponseUtils();

    @Override
    public boolean addUser(UserDTO userDTO) {
        User user = User.builder()
                .name(userDTO.getName())
                .password(userDTO.getPassword())
                .phone_number(userDTO.getPhone_number())
                .created_at(sdf.format(new Date()))
                .status(UserStatus.ONJOB.getCode())
                .build();
        userRepository.save(user);
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
        userRepository.save(user);
        return true;
    }

    @Override
    public User getUser(int id) {
        return userRepository.getOne(id);
    }

    @Override
    public boolean deleteUser(int id) {
        User user = getUser(id);
        if(user == null){
            return false;
        }
        user.setStatus(UserStatus.OFFJOB.getCode());
        user.setDeleted_at(sdf.format(new Date()));
        userRepository.save(user);
        return true;
    }

    @Override
    public ListResponse getUserList(int pageIndex, int pageSize, String name, int status) {
        List<User> userList = getUserList().stream().filter(u -> u.getStatus() == status && (u.getName().contains(name) || u.getPhone_number().contains(name)))
                .collect(Collectors.toList());
        return listResponseUtils.getListResponse(userList, pageIndex, pageSize);
    }

    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }
}
