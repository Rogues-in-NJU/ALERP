package edu.nju.alerp.dto;

import lombok.Data;

/**
 * @Description: 新增采购单-商品关联DTO
 * @Author: yangguan
 * @CreateDate: 2019-12-25 21:15
 */
@Data
public class PurchaseOrderProductDTO {

    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Double weight;
    private Double price;
    private Integer priceType;
    private Double cash;
}
