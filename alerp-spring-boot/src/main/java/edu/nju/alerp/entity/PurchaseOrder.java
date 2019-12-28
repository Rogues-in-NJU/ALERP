package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 采购单实体类
 * @Author: yangguan
 * @CreateDate: 2019-12-21 21:11
 */
@Data
@Builder
@Entity
@Table(name = "purchase_order")
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 单据编码
     */
    private String code;

    /**
     * 备注
     */
    private String description;

    /**
     * 供货商id
     */
    @Column(name = "supplier_id")
    private int supplierId;

    /**
     * 金额
     */
    private double cash;

    /**
     * 业务员
     */
    private String salesman;

    /**
     * 采购时间
     */
    @Column(name = "done_at")
    private String doneAt;

    /**
     * 状态（0：未完成，1：已完成， 2：已废弃）
     */
    private int status;

    /**
     * 创建时间
     */
    @Column(name = "create_at")
    private String createAt;

    /**
     * 创建者Id
     */
    @Column(name = "create_by")
    private int createBy;

    /**
     * 最后更新者id
     */
    @Column(name = "update_by")
    private int  updateBy;

    /**
     * 最后更新时间
     */
    @Column(name = "update_at")
    private String updateAt;

    /**
     * 废弃时间
     */
    @Column(name = "deleted_at")
    private String deletedAt;

    /**
     * 废弃者Id
     */
    @Column(name = "deleted_by")
    private int deletedBy;
}
