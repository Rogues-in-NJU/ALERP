package edu.nju.alerp.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 特惠商品详细信息
 * @Author: qianen.yin
 * @CreateDate: 2019-12-20 20:53
 */
@Data
@Builder
public class SpecialPrciesDO {
    private int id;
    private int productId;
    private String productName;
    private double price;
    private String createdAt;
    private int createdById;
    private String createdByName;
}
