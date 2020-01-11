package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 权限用户关联实体类
 * @Author: yangguan
 * @CreateDate: 2020-01-08 16:27
 */
@Builder
@Data
@Entity
@Table(name = "auth_user",
uniqueConstraints = {@UniqueConstraint(columnNames = {"auth_id", "user_id"})})
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 权限id
     */
    @Column(name = "auth_id")
    private int authId;

    /**
     * 用户Id
     */
    @Column(name = "user_id")
    private int userId;

    /**
     * 有无权限(0:没有， 1:有)
     */
    private int action;
}
