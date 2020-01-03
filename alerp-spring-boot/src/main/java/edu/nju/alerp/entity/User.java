package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 用户实体类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-16 00:44
 */
@Data
@Builder
@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    @Column(name = "phone_number")
    private String phoneNumber;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户状态
     */
    private int status;
    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private String createdAt;
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private String updatedAt;
    /**
     * 删除时间
     */
    @Column(name = "deleted_at")
    private String deletedAt;
}
