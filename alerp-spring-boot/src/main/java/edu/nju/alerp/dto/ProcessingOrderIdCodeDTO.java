package edu.nju.alerp.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 出货单id和code DTO
 * @Author: qianen.yin
 * @CreateDate: 2020-01-11 22:02
 */
@Data
@Builder
public class ProcessingOrderIdCodeDTO {
    private Integer processingOrderId;
    private String processingOrderCode;
}
