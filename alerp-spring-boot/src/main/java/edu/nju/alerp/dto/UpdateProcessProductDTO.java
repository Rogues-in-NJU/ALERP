package edu.nju.alerp.dto;


import lombok.Data;

/**
 * @Description: 新增/修改加工单的商品关联DTO
 * @Author: yangguan
 * @CreateDate: 2019-12-25 16:31
 */
@Data
public class UpdateProcessProductDTO {

    private Integer id;
    private Integer processingOrderId;
    private Integer productId;
    private String specification;
    private Integer quantity;
    private Double expectedWeight;
}
