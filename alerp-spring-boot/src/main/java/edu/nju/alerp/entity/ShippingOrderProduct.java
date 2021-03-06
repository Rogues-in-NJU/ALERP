package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public class ShippingOrderProduct {

    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
     * 算价方式
     */
    private int priceType;

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

    /**
     * 删除时间
     */
    @Column(name = "deleted_at")
    private String deletedAt;


}
