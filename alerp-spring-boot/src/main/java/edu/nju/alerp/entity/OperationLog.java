package edu.nju.alerp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Description: 用户操作日志
 * @Author: qianen.yin
 * @CreateDate: 2019-12-25 11:10
 */
@Data
@Builder
@Entity
@Table(name = "operation_log")
@AllArgsConstructor
@NoArgsConstructor
public class OperationLog {
    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private int userId;

    /**
     * 用户姓名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 操作时间
     */
    @Column(name = "created_at")
    private String createdAt;

    /**
     * 操作内容
     */
    private String description;
}
