package edu.nju.alerp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  ActionType {

    ACCESS(1, "通过权限"),
    DEFUSE(0, "拒绝权限");

    private int code;
    private String message;

    public static ActionType of(int code) throws Exception{
        for (ActionType item : ActionType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw  new Exception(); //todo error type
    }

}
