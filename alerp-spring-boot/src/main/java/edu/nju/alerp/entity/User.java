package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 用户实体类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-16 00:44
 */
@Data
@Builder
public class User {
    private Integer id;
    private String name;
    private String phone_number;
    private String password;
    private int status;
    private String created_at;
    private String updated_at;
    private String deleted_at;
}
