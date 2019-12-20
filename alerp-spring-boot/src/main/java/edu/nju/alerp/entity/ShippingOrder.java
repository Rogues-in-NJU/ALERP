package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @Description: 出货单实体类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-16 23:39
 */
@Data
@Builder
@Entity
public class ShippingOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
    private int customer_id;
    private int arrear_order_id;
    private int status;
    private double cash;
    private double floating_cash;
    private double receivable_cash;
    private int created_by;
    private String created_at;
    private int deleted_by;
    private String deleted_at;
    private int updated_by;
    private String updated_at;
}
