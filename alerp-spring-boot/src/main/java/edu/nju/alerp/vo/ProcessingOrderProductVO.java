package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 加工单-商品关联VO
 * @Author: yangguan
 * @CreateDate: 2019-12-24 16:45
 */
@Data
@Builder
public class ProcessingOrderProductVO {

    private Integer id;
    private Integer productId;
    private String productName;
    private Integer type;
    private Double density;
    private String productSpecification;
    private String specification;
    private Integer quantity;
    private Double expectedWeight;
}
