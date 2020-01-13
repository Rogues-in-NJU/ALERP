package edu.nju.alerp.vo;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

/**
 * 欠款统计VO
 *
 * @author luhailong
 * @date 2020/01/13
 */
@Data
public class OverdueCashVO {
    /**
     * 按人按月统计欠款：
     * [{
     * customerId: number,
     * customerName: string,
     * overdues: [{
     * month: string,
     * cash: number,
     * }]
     * total: number,
     * }]
     */
    private List<Map<String, Object>> customers;

    /**
     * 按月统计欠款总量:
     * {
     * overdues: [{
     * month: string,
     * cash: number
     * }],
     * total:number
     * }
     */
    private Map<String, Object> statistics;

    public OverdueCashVO() {
        this.customers = Lists.newArrayList();
        this.statistics = Maps.newHashMap();
    }
}
