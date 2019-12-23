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
@Table(name = "PurchaseOrderProduct")
public class PurchaseOrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int purchase_order_id;
    private int product_id;
    private int quantity;
    private double weight;
    private double price;
    private double cach;

}
