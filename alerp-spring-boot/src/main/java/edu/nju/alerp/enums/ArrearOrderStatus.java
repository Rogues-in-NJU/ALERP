package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收款单状态
 * @author luhailong
 * @date 2019/12/28
 */
@Getter
@AllArgsConstructor
public enum ArrearOrderStatus {
    UNCOLLECTED(1,"未收款"),
    PART_COLLECTED(2,"部分收款"),
    FINISHED(3, "已确认"),
    ABANDONED(4, "已废弃");

    private int code;
    private String message;

    public static ArrearOrderStatus of(int code) {
        for (ArrearOrderStatus item : ArrearOrderStatus.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }

}
