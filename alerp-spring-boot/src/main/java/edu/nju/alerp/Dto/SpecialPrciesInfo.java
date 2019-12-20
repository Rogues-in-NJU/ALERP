package edu.nju.alerp.Dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 特惠商品详细信息
 * @Author: qianen.yin
 * @CreateDate: 2019-12-20 20:53
 */
@Data
@Builder
public class SpecialPrciesInfo {
    private int id;
    private int productId;
    private String productName;
    private double price;
    private String createdAt;
    private int createdById;
    private String createdByName;
}
