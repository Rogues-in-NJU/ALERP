package edu.nju.alerp.vo;

import edu.nju.alerp.entity.ProcessingOrder;
import lombok.Data;

@Data
public class ProcessingOrderListVO {

    private Integer id;
    private String code;
    private Integer customerId;
    private String customerName;
    private Integer shippingOrderId;
    private String shippingOrderCode;
    private String salesman;
    private Integer status;
    private String createdAt;
    private Integer createdById;
    private String createdByName;

    public static ProcessingOrderListVO buildProcessingOrderListVO(ProcessingOrder processingOrder,
                                        String customerName, String createdByName) {
        ProcessingOrderListVO processingOrderListVO = new ProcessingOrderListVO();
        processingOrderListVO.setId(processingOrder.getId());
        processingOrderListVO.setCode(processingOrder.getCode());
        processingOrderListVO.setCustomerId(processingOrder.getCustomerId());
        processingOrderListVO.setCustomerName(customerName);
        processingOrderListVO.setShippingOrderId(processingOrder.getShippingOrderId());
        processingOrderListVO.setShippingOrderCode(processingOrder.getShippintOrderCode());
        processingOrderListVO.setSalesman(processingOrder.getSalesman());
        processingOrderListVO.setStatus(processingOrder.getStatus());
        processingOrderListVO.setCreatedAt(processingOrder.getCreateAt());
        processingOrderListVO.setCreatedById(processingOrder.getCreateBy());
        processingOrderListVO.setCreatedByName(createdByName);
        return processingOrderListVO;
    }
}
