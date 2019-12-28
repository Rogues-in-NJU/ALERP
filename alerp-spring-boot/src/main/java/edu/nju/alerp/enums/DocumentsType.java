package edu.nju.alerp.enums;

import lombok.Getter;

public enum DocumentsType {

    PROCESSING_ORDER("processing_order"),
    PURCHASE_ORDER("purchase_order"),
    SHIPPING_ORDER("shipping_order");

    @Getter
    private String name;

    DocumentsType(String name) {
        this.name = name;
    }

    public static DocumentsType of(String name) {
        for (DocumentsType item : DocumentsType.values()) {
            if (item.name.equals(name))
                return item;
        }
        return null;
    }
}
