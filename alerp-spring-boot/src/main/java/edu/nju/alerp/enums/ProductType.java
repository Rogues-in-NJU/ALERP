package edu.nju.alerp.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description: 商品类别
 * @Author: yangguan
 * @CreateDate: 2019-12-25 12:09
 */
@Getter
@AllArgsConstructor
public enum  ProductType {

    PANEL(0, "板材"),
    SHAPE(1, "型材"),
    STICK(2, "棒") {
        @Override
        protected double caculateVolume(String productSpecification,
                                     String specification) {
            return Math.PI * Math.pow(Double.valueOf(productSpecification), 2) * Double.valueOf(specification);
        }
    },
    LOSS(3, "损耗") {
        @Override
        protected double caculateVolume(String productSpecification,
                                     String specification) {
            return 1;
        }
    };

    private int code;
    private String message;

    public static ProductType of(int code) {
        for (ProductType item : ProductType.values()) {
            if (item.code == code)
                return item;
        }
        return null;
    }

    public double caculateWeight(String productSpecification, String specification
                                , int quantity, double density) {
        double volume = caculateVolume(productSpecification, specification);
        return volume * quantity * density;
    }

    protected double caculateVolume(String productSpecification,
                                  String specification) {
        String[] properties = specification.split("x");
        double result = 1;
        for (String property : properties) {
            result *= Double.valueOf(property);
        }
        return result;
    }

//    public boolean validateSpecification(String specification) {
//
//    }
}
