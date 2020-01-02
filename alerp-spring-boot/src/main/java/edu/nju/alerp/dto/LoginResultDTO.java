package edu.nju.alerp.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 登录结果回参
 * @Author: qianen.yin
 * @CreateDate: 2020-01-02 17:18
 */
@Data
@Builder
public class LoginResultDTO {
    private int code;
    private String result;
}
