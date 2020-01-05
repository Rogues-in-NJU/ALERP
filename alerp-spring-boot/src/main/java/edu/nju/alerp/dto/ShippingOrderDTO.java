package edu.nju.alerp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 出货单dto
 * @Author: qianen.yin
 * @CreateDate: 2019-12-23 19:14
 */
@Data
@Builder
public class ShippingOrderDTO {
    private int customerId;
    private double cash;
    private double floatingCash;
    private double receivableCash;
    private List<ShippingProductDTO> products;
}
