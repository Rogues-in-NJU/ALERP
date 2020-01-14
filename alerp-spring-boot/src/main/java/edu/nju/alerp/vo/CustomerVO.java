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
    private Integer id;
    private String name;
    private Integer type;
    private String shorthand;
    private Integer period;
    private Integer payDate;
    private String description;
    private Integer createdBy;
    private String createdAt;
    private Integer deletedBy;
    private String deletedAt;
    private Integer updatedBy;
    private String updatedAt;
    private List<SpecialPricesVO> specialPrices;
    private boolean inactive;
}
