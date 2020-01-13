package edu.nju.alerp.vo;

import java.util.List;

import edu.nju.alerp.dto.ReceiptRecordForArrearDTO;
import lombok.Builder;
import lombok.Data;

/**
 * 一张收款单的欠款明细详情VO
 * @author luhailong
 * @date 2019/12/29
 */
@Data
@Builder
public class ArrearDetailVO {
    /**
     * 收款单id
     */
    private int id;

    /**
     * 收款单编码
     */
    private String code;

    /**
     * 发票流水号
     */
    private String invoiceNumber;

    /**
     * 客户id
     */
    private int customerId;

    /**
     * 客户姓名
     */
    private String customerName;

    /**
     * 出货单id
     */
    private int shippingOrderId;

    /**
     * 出货单编码
     */
    private String shippingOrderCode;

    /**
     * 应收金额
     */
    private double receivableCash;

    /**
     * 实收金额
     */
    private double receivedCash;

    /**
     * 截止日期
     */
    private String dueDate;

    /**
     * 是否逾期
     */
    private boolean overDue;

    /**
     * 收款单状态，
     */
    private int status;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 创建者id
     */
    private int createdById;

    /**
     * 创建者姓名
     */
    private String createdByName;

    /**
     * 上次更新时间：作为乐观锁的版本号
     */
    private String updatedAt;

    /**
     * 收款单所含收款记录列表
     */
    private List<ReceiptRecordForArrearDTO> receiptRecordList;
}
