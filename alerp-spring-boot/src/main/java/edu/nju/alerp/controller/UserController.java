package edu.nju.alerp.controller;

import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ManagerSessions;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.LoginDTO;
import edu.nju.alerp.dto.LoginResultDTO;
import edu.nju.alerp.dto.PasswordDTO;
import edu.nju.alerp.dto.UserDTO;
import edu.nju.alerp.entity.*;
import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.LoginResult;
import edu.nju.alerp.enums.UserStatus;
import edu.nju.alerp.service.AuthService;
import edu.nju.alerp.service.OperationLogService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.util.PasswordUtil;
import edu.nju.alerp.vo.AuthUserVO;
import edu.nju.alerp.vo.UserInfoVO;
import edu.nju.alerp.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static edu.nju.alerp.Application.managerSession;

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
    @Autowired
    OperationLogService operationLogService;
    @Autowired
    AuthService authService;

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET, name = "删除用户")
    public ResponseResult<Integer> delete(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(userService.deleteUser(id));
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, name = "获取用户列表")
    public ResponseResult<ListResponse> list(@RequestParam(value = "pageIndex") int pageIndex,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                             @RequestParam(value = "status", required = false) Integer status) {
        List<Integer> userCityRelationList = userService.getUserListByCityId(CommonUtils.getCity());
        Page<UserVO> page = userService.getUserList(PageRequest.of(pageIndex - 1, pageSize), name, status, userCityRelationList);
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }

    /**
     * 获取用户详细信息
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, name = "获取用户详细信息")
    public ResponseResult<UserInfoVO> userInfo(@PathVariable("id") Integer id) {
        User user = userService.getUser(id);
        return ResponseResult.ok(generateUserInfo(user));
    }

    /**
     * 用户修改密码
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST, name = "修改当前用户密码")
    public ResponseResult<String> updatePassword(@RequestBody PasswordDTO passwordDTO) {
        User user = userService.getUser(CommonUtils.getUserId());
        if (user.getPassword().equals(PasswordUtil.getMD5(passwordDTO.getOldPassword()))) {
            user.setPassword(PasswordUtil.getMD5(passwordDTO.getNewPassword()));
            userService.updateUser(user);
            return ResponseResult.ok("修改密码成功!");
        }
        return ResponseResult.ok("原密码错误!");
    }

    /**
     * 获取当前登录用户详细信息
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/self", method = RequestMethod.GET, name = "获取登录用户详细信息")
    public ResponseResult<UserInfoVO> self() {
        User user = userService.getUser(CommonUtils.getUserId());
        return ResponseResult.ok(generateUserInfo(user));
    }

    private UserInfoVO generateUserInfo(User user) {
        List<AuthUserVO> authList = authService.queryAuthUserByUserId(user.getId());
        UserInfoVO userInfoVO = UserInfoVO.builder()
                .cities(userService.getCitiesByUserId(user.getId()))
                .authList(authList)
                .build();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    /**
     * 用户操作日志查询
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/operation-log/list", method = RequestMethod.GET, name = "用户操作日志查询")
    public ResponseResult<ListResponse> operationLogList(@RequestParam(value = "pageIndex") int pageIndex,
                                                         @RequestParam(value = "pageSize") int pageSize,
                                                         @RequestParam(value = "userName", required = false, defaultValue = "") String userName,
                                                         @RequestParam(value = "operationStartTime", required = false, defaultValue = "") String operationStartTime,
                                                         @RequestParam(value = "operationEndTime", required = false, defaultValue = "") String operationEndTime) {
        List<Integer> userList = userService.getUserListByCityId(CommonUtils.getCity());
        Page<OperationLog> page = operationLogService.getOpearationLogList(PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")), userList, userName, operationStartTime, operationEndTime);
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }

    /**
     * 新增用户
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST, name = "新增用户")
    public ResponseResult<Integer> addUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            int result = userService.saveUser(userDTO);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

    /**
     * 修改用户信息
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST, name = "修改用户信息")
    public ResponseResult<Integer> updateUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            int result = userService.saveUser(userDTO);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

    /**
     * 用户登录
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST, name = "用户登录")
    public ResponseResult<LoginResultDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            HttpSession session = CommonUtils.getHttpSession();
            LoginResultDTO loginResultDTO = userService.checkLogin(loginDTO);
            if (loginResultDTO.getCode() == LoginResult.SUCCESS.getCode()) {
                session.setAttribute("userId", loginResultDTO.getUserId());
                session.setAttribute("cityId", loginDTO.getCity());
            }
            return ResponseResult.ok(loginResultDTO);
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

    /**
     * 重置密码
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/reloadPassword/{id}", method = RequestMethod.GET, name = "重置密码")
    public ResponseResult<Integer> reloadPassword(@PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(userService.reloadPassWord(id));
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

    /**
     * 用户登出
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.GET, name = "用户登出")
    public ResponseResult<Boolean> logout() {
        try {
            HttpSession session = CommonUtils.getHttpSession();
            managerSession.getSessions().remove(CommonUtils.getUserId());
            if (session != null) {
                session.invalidate();
            }
            return ResponseResult.ok(true);
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

    /**
     * session过期校验
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/sessionValidation", method = RequestMethod.GET, name = "session过期校验")
    public ResponseResult<Boolean> sessionValidation() {
        try {
            HttpSession session = CommonUtils.getHttpSession();
            long second = (System.currentTimeMillis() - session.getCreationTime()) / 1000;
            return ResponseResult.ok(second < 7200);
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

}
