package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 顾客特惠价
 * @Author: qianen.yin
 * @CreateDate: 2019-12-18 16:50
 */

@Data
@Builder
public class SpecialPrices {

    private int id;
    private int productId;
    private String productName;
    private double price;
    private String createdAt;
    private int createdById;
    private String updateAt;
    private int updateById;
    private String deletedAt;
    private int deleteById;
}
