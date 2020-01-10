package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 商品平均单价
 * @Author: qianen.yin
 * @CreateDate: 2020-01-10 13:07
 */
@Data
@Builder
public class ProductAvgPriceVO {
    private int id;
    private String name;
    private double totalWeight;
    private double averagePrice;
}
