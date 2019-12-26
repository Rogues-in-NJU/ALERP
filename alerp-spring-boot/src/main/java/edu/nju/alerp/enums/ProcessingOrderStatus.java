package edu.nju.alerp.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 加工单状态枚举类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-25 11:44
 */
@Getter
@AllArgsConstructor
public enum  ProcessingOrderStatus {
    DRAFTING(0, "草稿中") {
        @Override
        public boolean abandonable() {
            return true;
        }

        @Override
        public boolean printable() {
            return true;
        }
    },
    UNFINISHED(1, "未完成") {
        @Override
        public boolean abandonable() {
            return true;
        }
    },
    FINISHED(2, "已完成"),
    ABANDONED(3, "已废弃");

    private int code;
    private String message;

    public static ProcessingOrderStatus of(int code) throws Exception{
        for (ProcessingOrderStatus item : ProcessingOrderStatus.values()) {
            if (item.code == code)
                return item;
        }
        throw new Exception(); //todo 不支持类型错误
    }

    public boolean abandonable() {
        return false;
    }

    public boolean printable() {
        return false;
    }
}
