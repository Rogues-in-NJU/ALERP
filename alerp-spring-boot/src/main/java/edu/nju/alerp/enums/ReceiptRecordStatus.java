package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收款记录状态枚举类
 *
 * @author luhailong
 * @date 2019/12/23
 */
@Getter
@AllArgsConstructor
public enum ReceiptRecordStatus {
    ABANDONED(0, "已废弃"),
    CONFIRMED(1, "已确认");

    private int code;
    private String message;

    public static ReceiptRecordStatus of(int code) {
        for (ReceiptRecordStatus item : ReceiptRecordStatus.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }

}
