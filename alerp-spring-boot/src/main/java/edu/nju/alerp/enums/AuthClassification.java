package edu.nju.alerp.enums;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum  AuthClassification {

    PRODUCT(1, "商品"),
    ARREAR(2, "欠款明细"),
    CUSTOMER(3, "客户"),
    EXPENSE(4, "支出"),
    PROCESS(5, "加工单"),
    PURCHASE(6, "采购单"),
    SHIP(7, "出货单"),
    SUMMARY(8, "汇总信息"),
    SUPPLIER(9, "供货商"),
    USER(10, "用户相关");

    private int code;
    private String message;

    public static AuthClassification of(int code) throws Exception{
        for (AuthClassification item : AuthClassification.values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw  new Exception();
    }

    public static AuthClassification getClassificationFromAuthId(int authId) throws Exception{
            return of((Integer) authToClassfication.get(authId));
    }


    public final static Map<Object, Object> authToClassfication =
            ImmutableMap.builder()
                    .put(1, 1)
                    .put(2, 1)
                    .put(3, 1)
                    .put(4, 1)
                    .put(9, 2)
                    .put(10, 2)
                    .put(11, 2)
                    .put(12, 2)
                    .put(13, 2)
                    .put(34, 2)
                    .put(35, 2)
                    .put(14, 3)
                    .put(15, 3)
                    .put(16, 3)
                    .put(17, 3)
                    .put(18, 4)
                    .put(19, 4)
                    .put(20, 4)
                    .put(21, 5)
                    .put(22, 5)
                    .put(23, 5)
                    .put(24, 5)
                    .put(25, 5)
                    .put(26, 5)
                    .put(27, 5)
                    .put(28, 6)
                    .put(29, 6)
                    .put(30, 6)
                    .put(31, 6)
                    .put(32, 6)
                    .put(33, 6)
                    .put(36, 7)
                    .put(37, 7)
                    .put(38, 7)
                    .put(39, 7)
                    .put(57, 7)
                    .put(40, 8)
                    .put(41, 8)
                    .put(42, 8)
                    .put(43, 9)
                    .put(44, 9)
                    .put(45, 9)
                    .put(46, 10)
                    .put(47, 10)
                    .put(48, 10)
                    .put(49, 10)
                    .put(50, 10)
                    .put(51, 10)
                    .put(52, 10)
                    .put(53, 10)
                    .put(54, 10)
                    .put(55, 10)
                    .put(56, 10)
                    .build();
}
