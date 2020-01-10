package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 顾客特惠价
 * @Author: qianen.yin
 * @CreateDate: 2019-12-18 16:50
 */

@Data
@Builder
@Entity
@Table(name = "special_prices")
@NoArgsConstructor
@AllArgsConstructor
public class SpecialPrice {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * 顾客id
     */
    private int customerId;
    /**
     * 商品id
     */
    private int productId;
    /**
     * 特惠价格
     */
    private double price;
    /**
     * 算价方式
     */
    private int priceType;
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
