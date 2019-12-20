package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 客户结算方式
 * @Author: qianen.yin
 * @CreateDate: 2019-12-18 16:56
 */
@Getter
@AllArgsConstructor
public enum CustomerType {

    CASH(1,"现金"),

    MONTH(2,"月结");

    private int code;
    private String message;


    public static CustomerType of(int code) {
        for (CustomerType item : CustomerType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}
