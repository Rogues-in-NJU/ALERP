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
    private int customerId;
    private int arrearOrderId;
    private double cash;
    private double floatingCash;
    private double receivableCash;
    private int status;
    private String city;
    private String createdAt;
    private int createdById;
    private String createdByName;
    private List<ProductVO> productVOList;
}
