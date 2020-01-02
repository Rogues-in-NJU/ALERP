package edu.nju.alerp.vo;

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
public class CustomerVO {
    private int id;
    private String name;
    private int type;
    private String shorthand;
    private String city;
    private int period;
    private int payDate;
    private String description;
    private int createdBy;
    private String createdAt;
    private int deletedBy;
    private String deletedAt;
    private int updatedBy;
    private String updatedAt;
    private List<SpecialPricesVO> specialPricesVOList;
}
