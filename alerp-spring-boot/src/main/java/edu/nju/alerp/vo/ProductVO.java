package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 商品VO
 * @Author: qianen.yin
 * @CreateDate: 2019-12-24 13:53
 */
@Data
@Builder
public class ProductVO {
    private Integer id;
    private Integer processingOrderId;
    private String processingOrderCode;
    private Integer productId;
    private String productName;
    private Integer type;
    private String specification;
    private Integer priceType;
    private Integer quantity;
    private Double expectedWeight;
    private Double price;
    private Double weight;
    private Double cash;
}
