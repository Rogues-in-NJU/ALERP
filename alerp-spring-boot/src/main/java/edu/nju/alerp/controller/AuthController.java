package edu.nju.alerp.controller;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.AuthDTO;
import edu.nju.alerp.dto.UpdateUserAuthDTO;
import edu.nju.alerp.entity.Auth;
import edu.nju.alerp.service.AuthService;
import edu.nju.alerp.vo.AuthUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 权限Controller
 * @Author: yangguan
 * @CreateDate: 2020-01-08 20:12
 */

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 新增/更新权限资源
     *
     * @param authDTO
     * @return
     */
    @InvokeControl
    @RequestMapping(value = "", method = RequestMethod.POST, name = "新增权限资源/更新权限资源")
    public ResponseResult<Integer> addOrUpdateAuth(@RequestBody AuthDTO authDTO) {
        return ResponseResult.ok(authService.addOrUpdateAuth(authDTO));
    }

    @InvokeControl
    @RequestMapping(value = "/list", method = RequestMethod.GET, name = "查询权限资源列表")
    public ResponseResult<List<Auth>> queryAuthList() {
        return ResponseResult.ok(authService.findAll());
    }

    @InvokeControl
    @RequestMapping(value = "/edit", method = RequestMethod.POST, name = "编辑某个用户的权限")
    public ResponseResult<Integer> editUserAuth(@RequestBody List<UpdateUserAuthDTO> updateAuths) {
        return ResponseResult.ok(authService.updateUserAuth(updateAuths));
    }

//    @RequestMapping(value = "/initial/{id}", method = RequestMethod.GET, name = "初始化某个用户权限")
//    public ResponseResult<Integer> initialUserAuthById(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
//        return ResponseResult.ok(authService.initialUserAuthByUserId(id));
//    }

    @InvokeControl
    @RequestMapping(value = "/userId/{id}", method = RequestMethod.GET, name = "查询某个用户的权限")
    public ResponseResult<List<AuthUserVO>> queryUserAuthByUserId(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(authService.queryAuthUserByUserId(id));
    }

    @InvokeControl
    @RequestMapping(value = "/initialAuth", method = RequestMethod.GET, name = "初始化权限资源")
    public ResponseResult<Integer> initialAuthResource() {
        return ResponseResult.ok(authService.initialAuthResource());
    }
}
