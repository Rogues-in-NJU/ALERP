package edu.nju.alerp.dto;

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
     * 收款人
     */
    private String salesman;

    /**
     * 备注
     */
    private String description;

    /**
     * 收款时间
     */
    private String doneAt;


}
