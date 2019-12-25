package edu.nju.alerp.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 采购单状态枚举类
 * @Author: yangguan
 * @CreateDate: 2019-12-25 17:53
 */
@Getter
@AllArgsConstructor
public enum  PurchaseOrderStatus {

    UNFINISHED(0, "未完成"),
    FINISHED(1, "已完成"),
    ABANDONED(2, "已废弃");

    private int code;
    private String message;

    public static PurchaseOrderStatus of(int code) throws Exception {
        for (PurchaseOrderStatus item : PurchaseOrderStatus.values()) {
            if (item.code == code)
                return item;
        }
        throw new Exception(); //todo 不支持类型错误
    }
}
