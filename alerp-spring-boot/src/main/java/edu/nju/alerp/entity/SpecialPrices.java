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
@Table(name = "SpecialPrices")
public class SpecialPrices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int customerId;
    private int productId;
    private double price;
    private String createdAt;
    private int createdById;
    private String updateAt;
    private int updateById;
    private String deletedAt;
    private int deleteById;
}
