package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 客户实体类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-16 23:20
 */
@Data
@Builder
@Entity
@Table(name = "customer")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 顾客姓名
     */
    private String name;
    /**
     * 付款类型：月结，现金
     */
    private int type;
    /**
     * 速记编号
     */
    private String shorthand;
    /**
     * 帐期
     */
    private int period;
    /**
     * 对账日
     */
    @Column(name = "pay_Date")
    private int payDate;
    /**
     * 城市
     */
    private int city;
    /**
     * 顾客描述
     */
    private String description;
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
