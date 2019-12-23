package edu.nju.alerp.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 出货单的商品dto
 * @Author: qianen.yin
 * @CreateDate: 2019-12-23 19:15
 */
@Data
@Builder
public class ShippingProductDTO {
    private int processingOrderId;
    private int productId;
    private String specification;
    private int quantity;
    private double price;
    private double expectedWeight;
    private double weight;
    private double cash;

}
