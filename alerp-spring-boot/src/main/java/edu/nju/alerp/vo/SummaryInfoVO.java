package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 汇总信息VO
 * @author luhailong
 * @date 2020/01/12
 */
@Data
@Builder
public class SummaryInfoVO {
    /**
     * 加工单铝材总重量
     */
    private Double processingOrderTotalWeight;

    /**
     * 出货单铝材总重量
     */
    private Double shippingOrderTotalWeight;

    /**
     * 新增出货单金额
     */
    private Double shippingOrderTotalCash;

    /**
     * 累计收款金额
     */
    private Double totalReceivedCash;

    /**
     * 累计逾期金额
     */
    private Double totalOverdueCash;

    /**
     * 累计采购单未付款金额
     */
    private Double purchaseOrderTotalUnpaidCash;

    /**
     * 生成加工单数
     */
    private Integer processingOrderTotalNum;

    /**
     * 生成出货单数
     */
    private Integer shippingOrderTotalNum;

    /**
     * 现金客户平均单价
     */
    private Double averagePriceMonthly;

    /**
     * 月结客户平均单价
     */
    private Double averagePriceCash;
}
