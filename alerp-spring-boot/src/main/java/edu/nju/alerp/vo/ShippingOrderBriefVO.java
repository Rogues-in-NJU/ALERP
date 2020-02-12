package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 出货单列表的VO
 * @Author: qianen.yin
 * @CreateDate: 2020-01-06 17:56
 */
@Data
@Builder
public class ShippingOrderBriefVO {
    private int id;
    private String code;
    private Integer customerId;
    private String customerName;
    private Integer arrearOrderId;
    private Integer status;
    private boolean tax;
    private boolean check;
    private Integer hasReconciliationed;
    private Integer city;
    private double cash;
    private double floatingCash;
    private double receivableCash;
    private int createdBy;
    private String createdAt;
    private Integer deletedBy;
    private String deletedAt;
    private Integer updatedBy;
    private String updatedAt;
}
