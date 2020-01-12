package edu.nju.alerp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 客户DTO
 * @Author: qianen.yin
 * @CreateDate: 2019-12-18 16:47
 */

@Data
@Builder
public class CustomerDTO {
    private Integer id;
    private String name;
    private String shorthand;
    private int type;
    private int period;
    private int payDate;
    private String description;
    private String updatedAt;
    private List<SpecialPricesDTO> specialPrices;
}
