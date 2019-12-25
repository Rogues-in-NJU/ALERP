package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 供货商状态
 * @Author: yangguan
 * @CreateDate: 2019-12-25 21:49
 */
@Getter
@AllArgsConstructor
public enum  SupplierStatus {

    NORMAL(0, "生效中"),
    DELETED(1, "已删除");

    private int code;
    private String message;

    public static SupplierStatus of(int code) {
        for (SupplierStatus item : SupplierStatus.values()) {
            if (item.code == code)
                return item;
        }
        return null;
    }
}
