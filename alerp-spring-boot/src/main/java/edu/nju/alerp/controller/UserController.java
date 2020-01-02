package edu.nju.alerp.controller;

import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.dto.LoginDTO;
import edu.nju.alerp.dto.LoginResultDTO;
import edu.nju.alerp.dto.UserDTO;
import edu.nju.alerp.entity.OperationLog;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.entity.User;
import edu.nju.alerp.entity.UserCityRelation;
import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.enums.LoginResult;
import edu.nju.alerp.service.OperationLogService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    @Autowired
    OperationLogService operationLogService;

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResponseResult<Integer> delete(
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
     * 用户操作日志查询
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/operation-log/list", method = RequestMethod.GET)
    public ResponseResult<ListResponse> operationLogList(@RequestParam(value = "pageIndex") int pageIndex,
                                                         @RequestParam(value = "pageSize") int pageSize,
                                                         @RequestParam(value = "userName") String userName,
                                                         @RequestParam(value = "operationStartTime") String operationStartTime,
                                                         @RequestParam(value = "operationEndTime") String operationEndTime) {

        Page<OperationLog> page = operationLogService.getOpearationLogList(PageRequest.of(pageIndex - 1, pageSize), userName, operationStartTime, operationEndTime);
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
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseResult<LoginResultDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            HttpSession session = CommonUtils.getHttpSession();
            LoginResultDTO loginResultDTO = null;
            User user = userService.getUserByPhoneNumber(loginDTO.getPhoneNumber());
            if (user == null) {
                loginResultDTO = LoginResultDTO.builder()
                        .code(LoginResult.NONE.getCode())
                        .result(LoginResult.NONE.getMessage())
                        .build();
                return ResponseResult.ok(loginResultDTO);
            }
            List<Integer> cityList = userService.getCitiesByUserId(user.getId());
            if (!cityList.contains(loginDTO.getCity())) {
                loginResultDTO = LoginResultDTO.builder()
                        .code(LoginResult.DENIED.getCode())
                        .result(LoginResult.DENIED.getMessage())
                        .build();
                return ResponseResult.ok(loginResultDTO);
            }
            boolean res = user.getPassword().equals(PasswordUtil.getMD5(loginDTO.getPassword()));
            if (res) {
                session.setAttribute("userId", user.getId());
                session.setAttribute("cityId", loginDTO.getCity());
                loginResultDTO = LoginResultDTO.builder()
                        .code(LoginResult.SUCCESS.getCode())
                        .result(LoginResult.SUCCESS.getMessage())
                        .build();
                return ResponseResult.ok(loginResultDTO);
            }
            loginResultDTO = LoginResultDTO.builder()
                    .code(LoginResult.SUCCESS.getCode())
                    .result(LoginResult.SUCCESS.getMessage())
                    .build();
            return ResponseResult.ok(loginResultDTO);
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

}
