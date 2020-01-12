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
    private Integer id;
    /**
     * 截止时间
     */
    private String dueDate;

    /**
     * 上次更新时间：作为乐观锁的版本号
     */
    private String updatedAt;

}
