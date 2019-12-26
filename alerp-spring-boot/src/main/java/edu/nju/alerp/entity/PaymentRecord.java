package edu.nju.alerp.entity;


import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description: 付款记录
 * @Author: yangguan
 * @CreateDate: 2019-12-25 21:55
 */
@Data
@Entity
@Builder
@Table(name = "payment_record")
public class PaymentRecord {

    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 采购单Id
     */
    @Column(name = "purchase_order_id", nullable = false)
    private int purchaseOrderId;

    /**
     * 付款金额
     */
    @Column(nullable = false)
    private double cash;

    /**
     * 状态（0：已确认， 1：已废弃）
     */
    @Column(nullable = false)
    private int status;

    /**
     * 备注
     */
    private String description;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 付款时间
     */
    @Column(name = "done_at", nullable = false)
    private String doneAt;

    /**
     * 创建时间
     */
    @Column(name = "create_at", nullable = false)
    private String createAt;

    /**
     * 创建者id
     */
    @Column(name = "create_by", nullable = false)
    private int createBy;

    /**
     * 废弃时间
     */
    @Column(name = "delete_at")
    private String deleteAt;

    /**
     * 废弃者id
     */
    @Column(name = "delete_by")
    private int deleteBy;
}
