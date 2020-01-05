package edu.nju.alerp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GeneratorType;

/**
 * 收款记录实体类
 *
 * @author luhailong
 * @date 2019/12/22
 */
@Data
@Builder
@Entity
@Table(name = "receipt_record")
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptRecord {
    /**
     * 收款记录id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 所在收款单id
     */
    private int arrearOrderId;

    /**
     * 状态：已确认，已废弃
     */
    private int status;

    /**
     * 收款金额
     */
    private double cash;

    /**
     * 收款人姓名
     */
    @Column(name = "salesman")
    private String salesman;

    /**
     * 备注
     */
    private String description;

    /**
     * 收款时间
     */
    @Column(name = "done_at")
    private String doneAt;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private String createdAt;

    /**
     * 创建者id
     */
    @Column(name = "created_by")
    private int createdBy;

    /**
     * 删除时间
     */
    @Column(name = "deleted_at")
    private String deletedAt;

    /**
     * 删除者id
     */
    @Column(name = "deleted_by")
    private int deletedBy;

}
