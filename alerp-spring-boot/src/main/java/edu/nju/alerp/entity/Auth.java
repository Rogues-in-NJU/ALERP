package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description: 权限
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:36
 */
@Data
@Builder
@Entity
@Table(name = "auth")
public class Auth {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 操作类型
     */
    private int action;
}
