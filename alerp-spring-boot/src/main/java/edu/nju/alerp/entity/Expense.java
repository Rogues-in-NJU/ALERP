package edu.nju.alerp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

/**
 * 公司支出实体类
 *
 * @author luhailong
 * @date 2019/12/21
 */
@Data
@Builder
@Entity
@Table(name = "expense")
public class Expense {
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
     * 单据名称
     */
    private String title;

    /**
     * 单据描述
     */
    private String description;

    /**
     * 支出金额
     */
    private double cash;

    /**
     * 支出时间
     */
    @Column(name = "done_at")
    private String doneAt;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private String createdAt;

    /**
     * 删除时间
     */
    @Column(name = "deleted_at")
    private String deletedAt;

    /**
     * 创建者id
     */
    @Column(name = "created_by")
    private int createdBy;

    /**
     * 删除者id
     */
    @Column(name = "deleted_by")
    private int deletedBy;
}
