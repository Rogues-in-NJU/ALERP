package edu.nju.alerp.dto;

import lombok.Data;

/**
 * @Description: 新增采购单付款记录DTO
 * @Author: yangguan
 * @CreateDate: 2019-12-23 21:28
 */
@Data
public class AddPaymentRecordDTO {

    private Integer purchaseOrderId;
    private Double cash;
    private String description;
    private String salesman;
    private String doneAt;
}
