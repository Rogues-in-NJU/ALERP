package edu.nju.alerp.service;

import edu.nju.alerp.dto.SummaryInfoDTO;
import edu.nju.alerp.vo.SummaryInfoVO;
import edu.nju.alerp.vo.SummaryProductInfoVO;

/**
 * @author luhailong
 * @date 2020/01/12
 */
public interface SummaryService {
    /**
     * 获取汇总信息
     * @return
     */
    SummaryInfoVO getSummaryInfo(SummaryInfoDTO dto);

    /**
     * 汇总商品汇总信息
     * @return
     */
    SummaryProductInfoVO getSummaryProductInfo();
}
