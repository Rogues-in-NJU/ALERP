package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 加工单详情返回页面
 * @Author: yangguan
 * @CreateDate: 2019-12-24 16:41
 */
@Data
@Builder
public class ProcessingOrderDetailVO {

    private Integer id;
    private String code;
    private Integer customerId;
    private String customerName;
    private Integer shippingOrderCode;
    private String salesman;
    private Integer status;
    private String createdAt;
    private String createdById;
    private String createdByName;
    private String updatedAt;
    private List<ProcessingOrderProductVO> products;
    private Double totalWeight;
}
