package edu.nju.alerp.dto;


import lombok.Data;

import java.util.List;

/**
 * @Description: 新增采购单DTO
 * @Author: yangguan
 * @CreateDate: 2019-12-25 21:15
 */
@Data
public class PurchaseOrderDTO {

    private String description;
    private Integer supplierId;
    private Double cash;
    private String salesman;
    private String doneAt;
    private List<PurchaseOrderProductDTO> products;
}
