package edu.nju.alerp.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

/**
 * @Description: 商品实体类
 * @Author: yangguan
 * @CreateDate: 2019-12-19 20:00
 */
@Data
@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String shorthand;
    private byte type;
    private double density;
    private String specification;
    private Date create_at;
    private int create_by;
    private Date delete_at;
    private int delete_by;
    private Date update_at;
    private int update_by;
}
