package edu.nju.alerp.enums;

import lombok.Getter;

public enum DocumentsType {

    PROCESSING_ORDER("processing_order"),
    PRODUCT("product"),
    PURCHASE_ORDER("purchase_order"),
    SHIPPING_ORDER("shipping_order");

    @Getter
    private String name;

    DocumentsType(String name) {
        this.name = name;
    }

    public static DocumentsType getDocumentsType(String name) {
        switch (name) {
            case "processing_order":
                return DocumentsType.PROCESSING_ORDER;
            case "product":
                return DocumentsType.PRODUCT;
            case "purchase_order":
                return DocumentsType.PURCHASE_ORDER;
            case "shipping_order":
                return DocumentsType.SHIPPING_ORDER;
        }
        return null;
    }
}
