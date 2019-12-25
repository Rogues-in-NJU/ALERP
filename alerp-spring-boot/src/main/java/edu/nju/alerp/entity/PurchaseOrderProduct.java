package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description: 采购单-商品关联实体类
 * @Author: yangguan
 * @CreateDate: 2019-12-21 21:11
 */
@Data
@Builder
@Entity
@Table(name = "Purchase_order_product")
public class PurchaseOrderProduct {

    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 采购单id
     */
    @Column(name = "purchase_order_id")
    private int purchaseOrderId;

    /**
     * 商品id
     */
    @Column(name = "product_id")
    private int productId;

    /**
     * 数量
     */
    private int quantity;

    /**
     * 重量
     */
    private double weight;

    /**
     * 单价
     */
    private double price;

    /**
     * 总金额
     */
    private double cach;

}
