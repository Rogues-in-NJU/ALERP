package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 采购单列表VO
 * @Author: yangguan
 * @CreateDate: 2019-12-25 21:15
 */
@Data
@Builder
public class PurchaseOrderListVO {

    private int id;
    private String code;
    private String description;
    private String supplierName;
    private double cash;
    private String saleman;
    private int status;
    private String doneAt;
    private String createdAt;
    private int createdById;
    private String createdByName;
}
