package edu.nju.alerp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 对账DTO
 * @Author: qianen.yin
 * @CreateDate: 2020-02-05 12:13
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationDTO {
    private List<Integer> shippingOrderIds;
    private Integer toState;
}
