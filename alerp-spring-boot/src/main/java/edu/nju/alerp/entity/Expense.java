package edu.nju.alerp.entity;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private String done_at;

    /**
     * 创建时间
     */
    private String created_at;

    /**
     * 删除时间
     */
    private String deleted_at;

    /**
     * 创建者id
     */
    private int created_by;

    /**
     * 删除者id
     */
    private int deleted_by;
}
