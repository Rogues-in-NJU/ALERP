package edu.nju.alerp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 汇总商品信息VO
 * @author luhailong
 * @date 2020/01/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryProductInfoVO {
    /**
     * 商品id
     */
    private Integer id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品总重量
     */
    private Double totalWeight;
    /**
     * 商品平均价格
     */
    private Double averagePrice;
}
