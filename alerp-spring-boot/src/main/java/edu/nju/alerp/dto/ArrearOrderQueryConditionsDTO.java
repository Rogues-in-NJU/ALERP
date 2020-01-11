package edu.nju.alerp.dto;

/**
 * 获取欠款详细列表DTO，获取方式传参不是json，弃用
 * @author luhailong
 * @date 2020/01/05
 */
public class ArrearOrderQueryConditionsDTO {

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 第几页
     */
    private Integer pageIndex;

    /**
     * 收款单id
     */
    private Integer id;

    /**
     * 客户姓名（名字或简称）
     */
    private String customerName;

    /**
     * 发票流水号
     */
    private String invoiceNumber;

    /**
     * 出货单id
     */
    private Integer shippingOrderId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间：起始时间
     */
    private String createAtStartTime;

    /**
     * 创建时间：结束时间
     */
    private String createAtEndTime;

}
