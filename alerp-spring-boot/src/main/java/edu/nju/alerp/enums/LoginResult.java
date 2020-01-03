package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 登录状态
 * @Author: qianen.yin
 * @CreateDate: 2020-01-02 16:56
 */
@Getter
@AllArgsConstructor
public enum LoginResult {

    SUCCESS(1, "登录成功"),
    INCORRECT(2, "密码错误"),
    NONE(3, "没有该手机号的用户"),
    DENIED(4, "没有该城市访问权限"),
    OFFJOB(5, "用户已离职");

    private int code;
    private String message;


    public static LoginResult of(int code) {
        for (LoginResult item : LoginResult.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}
