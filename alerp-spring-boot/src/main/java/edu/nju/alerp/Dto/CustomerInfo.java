package edu.nju.alerp.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 客户详细信息
 * @Author: qianen.yin
 * @CreateDate: 2019-12-18 17:19
 */
@Data
@Builder
public class CustomerInfo {
    private int id;
    private String name;
    private int type;
    private String shorthand;
    private int period;
    private int pay_date;
    private String description;
    private int created_by;
    private String created_at;
    private int deleted_by;
    private String deleted_at;
    private int updated_by;
    private String updated_at;
    private List<SpecialPrciesInfo> specialPrciesInfoList;
}
