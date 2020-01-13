package edu.nju.alerp.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 修改收款单发票流水号DTO
 *
 * @author luhailong
 * @date 2020/01/12
 */
@Data
@Builder
public class ArrearOrderInvoiceNumberDTO {

    /**
     * 收款单id
     */
    private Integer id;

    /**
     * 发票流水号
     */
    private String invoiceNumber;

    /**
     * 上次更新时间：作为乐观锁的版本号
     */
    private String updatedAt;
}
