package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 权限类型
 * @Author: yangguan
 * @CreateDate: 2019-12-27 11:55
 */
@Getter
@AllArgsConstructor
public enum  AuthType {

    READ(0, "可读权限"),
    WRIGHT(1, "可写权限"),
    ABANDON(2, "废弃权限");

    private int code;
    private String message;

    public static AuthType of(int code) throws Exception{
        for (AuthType item : AuthType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw  new Exception(); //todo error type
    }
}
