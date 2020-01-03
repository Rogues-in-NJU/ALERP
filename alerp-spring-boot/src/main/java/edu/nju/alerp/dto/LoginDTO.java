package edu.nju.alerp.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 登录DTO
 * @Author: qianen.yin
 * @CreateDate: 2020-01-02 16:10
 */
@Data
@Builder
public class LoginDTO {
    private String phoneNumber;
    private String password;
    private int city;
}

