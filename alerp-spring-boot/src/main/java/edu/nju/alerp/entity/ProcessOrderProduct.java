package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description: 加工单商品关联实体类
 * @Author: yangguan
 * @CreateDate: 2019-12-19 20:00
 */
@Data
@Builder
@Entity
@Table(name = "process_order_product")
public class ProcessOrderProduct {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 加工单id
     */
    @Column(name = "process_order_id")
    private int processOrderId;

    /**
     * 商品id
     */
    @Column(name = "product_id")
    private int productId;

    /**
     * 规格
     */
    @Column(name = "specification")
    private String specification;

    /**
     * 数量
     */
    @Column(name = "quantity")
    private int quantity;

    /**
     * 厘重
     */
    @Column(name = "expected_weight")
    private double expectedWeight;
}
