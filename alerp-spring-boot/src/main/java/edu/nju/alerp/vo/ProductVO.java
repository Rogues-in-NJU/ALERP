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
    private int id;
    private int processingOrderId;
    private int productId;
    private String productName;
    private int type;
    private String specification;
    private int quantity;
    private double expectedWeight;
    private double price;
    private double weight;
    private double cash;
}
