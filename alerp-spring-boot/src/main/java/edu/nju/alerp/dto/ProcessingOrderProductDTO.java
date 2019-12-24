package edu.nju.alerp.dto;

import lombok.Data;

/**
 * @Description: 新增加工单-商品关联DTO
 * @Author: yangguan
 * @CreateDate: 2019-12-24 17:40
 */
@Data
public class ProcessingOrderProductDTO {
    private int productId;
    private String specification;
    private int quantity;
    private int expectedWeight;
}
