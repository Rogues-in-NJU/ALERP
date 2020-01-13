package edu.nju.alerp.service.impl;

import edu.nju.alerp.service.ArrearOrderService;
import edu.nju.alerp.service.SummaryService;
import edu.nju.alerp.vo.SummaryInfoVO;
import edu.nju.alerp.vo.SummaryProductInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luhailong
 * @date 2020/01/12
 */
@Service
public class SummaryServiceImpl implements SummaryService {

    @Autowired
    private ArrearOrderService arrearOrderService;

    @Override
    public SummaryInfoVO getSummaryInfo() {
        // fixme:前端接口目前还没修改，之后会有startTime和endTime
        SummaryInfoVO summaryInfoVO = SummaryInfoVO.builder().
            totalReceivedCash(arrearOrderService.queryTotalReceivedCash(null,null)).
            totalOverdueCash(arrearOrderService.queryTotalOverdueCash(null,null)).
            build();
        return summaryInfoVO;
    }

    @Override
    public SummaryProductInfoVO getSummaryProductInfo() {
        return null;
    }
}
