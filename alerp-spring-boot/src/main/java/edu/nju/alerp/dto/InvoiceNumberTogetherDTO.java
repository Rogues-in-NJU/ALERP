package edu.nju.alerp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量输入发票流水号DTO
 * @author luhailong
 * @date 2020/02/05
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceNumberTogetherDTO {
    /**
     * 出货单列表
     */
    private List<Integer> shippingOrderIds;

    /**
     * 该列表所有的出货单对应的发票号
     */
    private String invoiceNumber;
}
