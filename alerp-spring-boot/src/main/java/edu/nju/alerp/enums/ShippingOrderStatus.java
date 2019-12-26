package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 出货单状态枚举类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-23 19:34
 */
@Getter
@AllArgsConstructor
public enum ShippingOrderStatus {
    SHIPPIED(0,"已出货"),
    FINISHED(1, "已完成"),
    ABANDONED(2, "已废弃");

    private int code;
    private String message;


    public static ShippingOrderStatus of(int code) {
        for (ShippingOrderStatus item : ShippingOrderStatus.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}
