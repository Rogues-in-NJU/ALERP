package edu.nju.alerp.dto;


import lombok.Data;

import java.util.List;

/**
 * @Description: 新增加工单DTO
 * @Author: yangguan
 * @CreateDate: 2019-12-24 17:40
 */
@Data
public class ProcessingOrderDTO {

    private int customerId;
    private String salesman;
    private List<ProcessingOrderProductDTO> products;
}
