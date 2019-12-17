package edu.nju.alerp.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 客户实体类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-16 23:20
 */
@Data
@Builder
public class Customer {
    private int id;
    private String name;
    private int type;
    private String shorthand;
    //帐期
    private int period;
    //对账日
    private int pay_date;
    private String description;
    private int created_by;
    private String created_at;
    private int deleted_by;
    private String deleted_at;
    private int updated_by;
    private String updated_at;
}
