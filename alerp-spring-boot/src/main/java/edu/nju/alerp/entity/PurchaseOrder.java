package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description: 采购单实体类
 * @Author: yangguan
 * @CreateDate: 2019-12-21 21:11
 */
@Data
@Builder
@Entity
@Table(name = "PurchaseOrder")
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String code;
    private String description;
    private String purchaing_company;
    private double cash;
    private String salesman;
    private Date done_at;
    private int status;
    private Date create_at;
    private int create_by;
    private Date deleted_at;
    private int deleted_by;
}
