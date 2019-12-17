package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 用户状态枚举类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:28
 */

@Getter
@AllArgsConstructor
public enum UserStatus {

    ONJOB(1, "在职"),
    OFFJOB(2, "离职");

    private int code;
    private String message;


    public static UserStatus of(int code) {
        for (UserStatus item : UserStatus.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}
