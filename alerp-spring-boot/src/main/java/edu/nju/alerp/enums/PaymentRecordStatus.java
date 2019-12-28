package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 付款记录状态
 * @Author: yangguan
 * @CreateDate: 2019-12-26 14:12
 */
@Getter
@AllArgsConstructor
public enum PaymentRecordStatus {

    ABANDONED(0, "已废弃"),
    CONFIRMED(1, "已确认");

    private int code;
    private String message;

    public static PaymentRecordStatus of(int code) {
        for (PaymentRecordStatus item : PaymentRecordStatus.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}
