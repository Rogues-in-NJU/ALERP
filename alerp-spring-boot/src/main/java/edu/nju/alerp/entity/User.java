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
    private String userid;
    private String userName;
    private String phoneNum;

}
