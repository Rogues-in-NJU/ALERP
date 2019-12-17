package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 权限
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:36
 */
@Data
@Builder
public class Auth {
    private String id;
    private int action;
}
