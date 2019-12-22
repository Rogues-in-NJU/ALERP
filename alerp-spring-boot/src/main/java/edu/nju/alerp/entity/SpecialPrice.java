package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description: 顾客特惠价
 * @Author: qianen.yin
 * @CreateDate: 2019-12-18 16:50
 */

@Data
@Builder
@Entity
@Table(name = "special_prices")
public class SpecialPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int customerId;
    private int productId;
    private double price;
    private String createdAt;
    private int createdById;
    private String updateAt;
    private int updateBy;
    private String deleteAt;
    private int deleteBy;
}
