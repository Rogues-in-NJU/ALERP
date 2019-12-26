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
@Table(name="user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String password;
    private int status;
    @Column(name = "created_at")
    private String createdAt;
    @Column(name = "updated_at")
    private String updatedAt;
    @Column(name = "deleted_at")
    private String deletedAt;
}
