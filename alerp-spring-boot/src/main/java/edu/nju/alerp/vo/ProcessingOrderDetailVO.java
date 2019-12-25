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

    private int id;
    private String code;
    private int customerId;
    private String customerName;
    private int shippingOrderId;
    private String salesman;
    private int status;
    private String createdAt;
    private String createdById;
    private String createdByName;
    private List<ProcessingOrderProductVO> products;

}
