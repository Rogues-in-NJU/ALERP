package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description: 出货单-商品关联
 * @Author: qianen.yin
 * @CreateDate: 2019-12-24 14:26
 */
@Data
@Builder
@Entity
@Table(name = "shipping_order_product")
public class ShippingOrderProduct {

    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 出货单id
     */
    @Column(name = "shipping_order_id")
    private int shippingOrderId;

    /**
     * 加工单id
     */
    @Column(name = "processing_order_id")
    private int processingOrderId;

    /**
     * 商品id
     */
    @Column(name = "product_id")
    private int productId;

    /**
     * 规格
     */
    private String specification;

    /**
     * 数量
     */
    private int quantity;

    /**
     * 单价
     */
    private double price;

    /**
     * 厘重
     */
    @Column(name = "expected_weight")
    private double expectedWeight;

    /**
     * 实际称重
     */
    private double weight;

    /**
     * 总金额
     */
    private double cash;


}
