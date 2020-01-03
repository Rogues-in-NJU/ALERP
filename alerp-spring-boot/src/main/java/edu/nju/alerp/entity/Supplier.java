package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 供货商实体类
 * @Author: yangguan
 * @CreateDate: 2019-12-25 21:49
 */
@Data
@Entity
@Builder
@Table(name = "supplier")
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {

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
     * 状态(0：生效， 1：已删除)
     */
    private int status;

    /**
     * 城市
     */
    private String city;

    /**
     * 备注
     */
    private String description;

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

}
