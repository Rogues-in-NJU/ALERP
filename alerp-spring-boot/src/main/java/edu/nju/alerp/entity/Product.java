package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description: 商品实体类
 * @Author: yangguan
 * @CreateDate: 2019-12-19 20:00
 */
@Data
@Entity
@Builder
@Table(name = "product")
public class Product {

    /**
     * 自增id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 名称
     */
    private String name;

    /**
     * 速记编号
     */
    private String shorthand;

    /**
     * 类别（0：板材、 1：型材、 2：棒、 3：损耗）
     */
    private int type;

    /**
     * 密度
     */
    private double density;

    /**
     * 规格
     */
    private String specification;

    /**
     * 创建时间
     */
    @Column(name = "create_at")
    private String createAt;

    /**
     * 创建者id
     */
    @Column(name = "create_by")
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
     * 最后修改时间
     */
    @Column(name = "update_at")
    private String updateAt;

    /**
     * 最后修改者id
     */
    @Column(name = "update_by")
    private int updateBy;
}
