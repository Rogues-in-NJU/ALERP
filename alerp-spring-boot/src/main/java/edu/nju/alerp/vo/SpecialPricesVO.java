package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 特惠商品详细信息
 * @Author: qianen.yin
 * @CreateDate: 2019-12-20 20:53
 */
@Data
@Builder
public class SpecialPricesVO {
    private Integer id;
    private Integer productId;
    private String productName;
    private double price;
    private String createdAt;
    private Integer createdById;
    private String createdByName;
}
