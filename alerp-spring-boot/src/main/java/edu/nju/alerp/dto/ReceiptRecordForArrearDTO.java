package edu.nju.alerp.dto;

/**
 * 欠款明细详情中的收款记录DTO
 *
 * @author luhailong
 * @date 2019/12/29
 */
public class ReceiptRecordForArrearDTO {
    /**
     * 收款记录id
     */
    private int id;

    /**
     * 收款记录状态，ReceiptRecordStatus
     */
    private int status;

    /**
     * 收款金额
     */
    private double cash;

    /**
     * 收款人姓名
     */
    private String salesman;

    /**
     * 备注
     */
    private String description;

    /**
     * 收款时间
     */
    private String doneAt;

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

}
