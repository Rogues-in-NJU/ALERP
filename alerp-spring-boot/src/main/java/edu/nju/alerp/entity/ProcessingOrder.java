package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description: 加工单实体类
 * @Author: yangguan
 * @CreateDate: 2019-12-19 20:00
 */
@Data
@Entity
@Builder
@Table(name = "processing_order")
public class ProcessingOrder {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 单据编码
     */
    @Column(unique = true, nullable = false)
    private String code;

    /**
     * 状态（0：草稿中， 1：未完成， 2：已完成， 3：已废弃）
     */
    @Column(nullable = false)
    private int status;

    /**
     * 客户id
     */
    @Column(name = "customer_id", nullable = false)
    private int customerId;
//
//    /**
//     * 客户名字
//     * */
//    @Column(name = "customer_name")
//    private String customerName;
//
//    /**
//     * 客户的速记编号
//     * */
//    @Column(name = "shorthand")
//    private String shorthand;

    /**
     * 出货单id
     */
    @Column(name = "shipping_order_id")
    private int shippingOrderId;

    /**
     * 业务员
     */
    private String salesman;

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

    /**
     * 最后更新时间
     */
    @Column(name = "update_at", nullable =  false)
    private String updateAt;

    /**
     * 最后更新者id
     */
    @Column(name = "update_by", nullable = false)
    private int updateBy;

}
