package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 条件搜索得到的收款单列表里的VO
 * @author luhailong
 * @date 2020/01/12
 */
@Data
@Builder
public class ArrearOrderListContentVO {
    /**
     * 收款单id
     */
    private Integer id;
    /**
     * 收款单编码
     */
    private String code;
    /**
     * 客户id
     */
    private Integer customerId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 对应的出货单id
     */
    private Integer shippingOrderId;
    /**
     * 对应的出货单编码
     */
    private String shippingOrderCode;
    /**
     * 应收金额
     */
    private Double receivableCash;
    /**
     * 实收金额
     */
    private Double receivedCash;
    /**
     * 截止日期
     */
    private String dueDate;
    /**
     * 是否逾期
     */
    private Boolean overDue;
    /**
     * 收款单状态：ArrearOrderStatus
     */
    private Integer status;
    /**
     * 创建时间
     */
    private String createdAt;
    /**
     * 创建者id
     */
    private Integer createdById;
    /**
     * 创建者姓名
     */
    private String createdByName;
}
