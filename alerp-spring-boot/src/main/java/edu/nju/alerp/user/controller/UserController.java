package edu.nju.alerp.user.controller;

import edu.nju.alerp.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 用户Controller层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-15 23:41
 */

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    @ResponseBody
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseResult<Integer> get(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {

        return ResponseResult.ok(1);
    }
}
