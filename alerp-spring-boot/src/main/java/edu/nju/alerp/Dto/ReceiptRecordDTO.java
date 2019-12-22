package edu.nju.alerp.Dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author luhailong
 * @date 2019/12/22
 */
@Data
@Builder
public class ReceiptRecordDTO {
    /**
     * 所在收款单id
     */
    private int arrearOrderId;

    /**
     * 收款金额
     */
    private double cash;

    /**
     * 收款人id
     */
    private int salesmanId;

    /**
     * 备注
     */
    private String description;

    /**
     * 收款时间
     */
    private String doneAt;


}
