package edu.nju.alerp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收款单实体类
 *
 * @author luhailong
 * @date 2019/12/28
 */
@Data
@Builder
@Entity
@Table(name = "arrear_order")
@NoArgsConstructor
@AllArgsConstructor
public class ArrearOrder {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 收款单编码
     */
    private String code;

    /**
     * 所属城市
     */
    private int city;

    /**
     * 收款单状态：未收款，部分收款，已完成，已废弃
     */
    private int status;

    /**
     * 发票流水号
     */
    @Column(name = "invoice_number")
    private String invoiceNumber;

    /**
     * 客户id
     */
    @Column(name = "customer_id")
    private int customerId;

    /**
     * 应收金额
     */
    @Column(name = "receivable_cash")
    private double receivableCash;

    /**
     * 实收金额
     */
    @Column(name = "received_cash")
    private double receivedCash;

    /**
     * 截止日期
     */
    @Column(name = "due_date")
    private String dueDate;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private String createdAt;

    /**
     * 创建者id
     */
    @Column(name = "created_by")
    private int createdBy;

    /**
     * 删除时间
     */
    @Column(name = "deleted_at")
    private String deletedAt;

    /**
     * 删除者id
     */
    @Column(name = "deleted_by")
    private int deletedBy;

    /**
     * 最近修改时间
     */
    @Column(name = "updated_at")
    private String updatedAt;

    /**
     * 修改者id
     */
    @Column(name = "updated_by")
    private int updatedBy;


}
