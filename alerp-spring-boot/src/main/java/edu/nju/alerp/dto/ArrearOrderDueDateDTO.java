package edu.nju.alerp.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 修改收款单截止时间DTO
 * @author luhailong
 * @date 2019/12/28
 */
@Data
@Builder
public class ArrearOrderDueDateDTO {
    /**
     * 收款单id
     */
    private int id;
    /**
     * 截止时间
     */
    private String dueDate;
}
