package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @Description: 权限
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:36
 */
@Data
@Builder
@Entity
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private int action;
}
