package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 出货单实体类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-16 23:39
 */
@Data
@Builder
@Entity
@Table(name = "shipping_order")
@NoArgsConstructor
@AllArgsConstructor
public class ShippingOrder {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 单据编码
     */
    private String code;
    /**
     * 顾客id
     */
    @Column(name = "customer_id")
    private int customerId;
    /**
     * 收款单id
     */
    @Column(name = "arrear_order_id")
    private int arrearOrderId;
    /**
     * 单据状态：已出货/已完成/已废弃
     */
    private int status;
    /**
     * 总金额
     */
    private double cash;
    /**
     * 城市
     */
    private int city;
    /**
     * 是否含税
     */
    private boolean tax;
    /**
     *浮动金额
     */
    @Column(name="floating_cash")
    private double floatingCash;
    /**
     *实收金额
     */
    @Column(name="receivable_cash")
    private double receivableCash;
    /**
     * 创建者id
     */
    @Column(name = "created_by")
    private int createdBy;
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private String createdAt;
    /**
     * 删除者id
     */
    @Column(name = "deleted_by")
    private int deletedBy;
    /**
     * 删除时间
     */
    @Column(name = "deleted_at")
    private String deletedAt;
    /**
     * 更新者id
     */
    @Column(name = "update_by")
    private int updatedBy;
    /**
     * 更新时间
     */
    @Column(name = "update_at")
    private String updatedAt;
}
