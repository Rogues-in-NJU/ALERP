package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 出货单vo
 * @Author: qianen.yin
 * @CreateDate: 2019-12-24 13:52
 */
@Data
@Builder
public class ShippingOrderVO {
    private int id;
    private String code;
    private Integer customerId;
    private String customerName;
    private Integer arrearOrderId;
    private String arrearOrderCode;
    private boolean tax;
    private boolean check;
    private int hasReconciliationed;
    private double floatingCash;
    private double receivableCash;
    private Integer status;
    private String city;
    private String createdAt;
    private Integer createdBy;
    private String deletedAt;
    private String deletedByName;
    private String createdByName;
    private double totalWeight;
    private List<ProcessOrderIdCodeVO> processingOrderIdsCodes;
    private List<ProductVO> products;
}
