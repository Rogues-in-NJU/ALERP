package edu.nju.alerp.vo;

import edu.nju.alerp.entity.PaymentRecord;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 采购单详情VO
 * @Author: yangguan
 * @CreateDate: 2019-12-23 21:33
 */
@Data
@Builder
public class PurchaseOrderDetailVO {

    private Integer id;
    private String code;
    private String description;
    private Integer supplierId;
    private String supplierName;
    private Double cash;
    private String salesman;
    private Integer status;
    private String doneAt;
    private String createdAt;
    private Integer createdById;
    private String createdByName;
    private List<PurchaseProductVO> products;
    private List<PaymentRecord> paymentRecords;

}
