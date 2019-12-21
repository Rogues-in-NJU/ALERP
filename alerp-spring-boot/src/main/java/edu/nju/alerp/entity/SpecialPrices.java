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
    private String created_at;
    private int created_by;
    private String update_at;
    private int update_by;
    private String delete_at;
    private int delete_by;
}
