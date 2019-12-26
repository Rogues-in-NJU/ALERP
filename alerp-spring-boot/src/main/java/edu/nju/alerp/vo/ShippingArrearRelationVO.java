package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 出货单与欠款单vo
 * @Author: qianen.yin
 * @CreateDate: 2019-12-26 14:50
 */
@Data
@Builder
public class ShippingArrearRelationVO {
    private int shippingOrderId;
    private int arrearOrderId;
}
