package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 加工单id code关联VO
 * @Author: qianen.yin
 * @CreateDate: 2020-01-10 17:55
 */
@Data
@Builder
public class ProcessOrderIdCodeVO {
    private Integer processingOrderId;
    private String processingOrderCode;
}
