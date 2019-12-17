package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @Description: 错误枚举值
 * @Author: qianen.yin
 * @CreateDate: 2019-12-15 23:49
 */
@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    SUCCESS(10000, "成功"),
    SERVER_ERROR(10001, "系统错误"),
    SERVER_TIMEOUT(10002, "服务超时"),
    SERVER_LIMIT(10003, "服务限流"),
    ILLEGAL_PARAM(10004, "参数错误"),
    API_NOT_EXIST(10005, "接口不存在"),
    HTTP_METHOD_NOT_SUPPORT(10006, "HTTP METHOD不支持"),
    ILLEGAL_REQUEST(10007, "非法请求"),
    ILLEGAL_USER(10008, "非法用户"),
    AUTH_FAIL(10009, "没有权限"),
    OTHER_ERROR(20000, "其他异常"),
    FIELD_EDIT_FORBID(20000, "禁止更改字段");

    private int code;
    private String message;


    public static ExceptionEnum of(int code) {
        for (ExceptionEnum item : ExceptionEnum.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}