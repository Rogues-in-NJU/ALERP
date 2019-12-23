package edu.nju.alerp.controller;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.dto.UserDTO;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.ListResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult<ListResponse> list(@RequestParam(value = "pageIndex") int pageIndex,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value = "name") String name,
                                             @RequestParam(value = "status") int status) {
        Page<User> page = userService.getUserList(PageRequest.of(pageIndex - 1, pageSize), name, status);
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }

    /**
     * 新增用户/修改用户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<Integer> saveUser(@Valid @RequestBody UserDTO userDTO) {
        int result = 0;
        if (userDTO.getId() == null) {
            result = userService.addUser(userDTO);
        } else {
            result = userService.updateUser(userDTO);
        }
        return ResponseResult.ok(result);
    }
}
