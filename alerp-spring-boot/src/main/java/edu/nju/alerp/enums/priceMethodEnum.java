package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 计价方式枚举
 * @Author: qianen.yin
 * @CreateDate: 2020-01-08 15:11
 */
@Getter
@AllArgsConstructor
public enum priceMethodEnum {
    WEIGHT(1, "重量"),
    NUM(2, "件数");
    private int code;
    private String message;


    public static priceMethodEnum of(int code) {
        for (priceMethodEnum item : priceMethodEnum.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}
