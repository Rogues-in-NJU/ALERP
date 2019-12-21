package edu.nju.alerp.Controller;

import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.Dto.UserDTO;
import edu.nju.alerp.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 用户Controller层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-15 23:41
 */

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResponseResult<Boolean> delete(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(userService.deleteUser(id));
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseResult<List<User>> list(@RequestParam(value = "pageIndex") int pageIndex,
                                           @RequestParam(value = "pageSize") int pageSize,
                                           @RequestParam(value = "name") String name,
                                           @RequestParam(value = "status") int status) {
        List<User> userList = userService.getUserList();
        return ResponseResult.ok(userList);
    }

    /**
     * 新增用户/修改用户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<List<User>> saveUser(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getId() == null) {
            userService.addUser(userDTO);
        } else {
            userService.updateUser(userDTO);
        }
        List<User> userList = userService.getUserList();
        return ResponseResult.ok(userList);
    }
}
