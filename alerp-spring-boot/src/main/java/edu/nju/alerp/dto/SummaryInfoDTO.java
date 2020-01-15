package edu.nju.alerp.dto;

import lombok.Data;

/**
 * 汇总信息DTO，前端传参：开始时间和结束时间
 *
 * @author luhailong
 * @date 2020/01/15
 */
@Data
public class SummaryInfoDTO {
    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
}
