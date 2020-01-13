package edu.nju.alerp.service.impl;

import edu.nju.alerp.service.*;
import edu.nju.alerp.vo.SummaryInfoVO;
import edu.nju.alerp.vo.SummaryProductInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luhailong
 * @date 2020/01/12
 */
@Service
public class SummaryServiceImpl implements SummaryService {

    @Autowired
    private ArrearOrderService arrearOrderService;

    @Autowired
    private ProcessOrderService processOrderService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private ShippingOrderService shippingOrderService;

    @Autowired
    private CustomerService customerService;

    @Override
    public SummaryInfoVO getSummaryInfo() {
        // fixme:前端接口目前还没修改，之后会有startTime和endTime
        String startDate = "";
        String endDate = "";
        SummaryInfoVO summaryInfoVO = SummaryInfoVO.builder().
                totalReceivedCash(arrearOrderService.queryTotalReceivedCash(null, null)).
                totalOverdueCash(arrearOrderService.queryTotalOverdueCash(null, null)).
                processingOrderTotalWeight(processOrderService.queryTotalWeight(null, null)).
                purchaseOrderTotalUnpaidCash(purchaseOrderService.queryUnPaidCash(null, null)).
                shippingOrderTotalWeight(shippingOrderService.queryTotalWeight(null, null)).
                build();
        int processingOrderTotalNum = shippingOrderService.findTotalNum(startDate, endDate);
        int shippingOrderTotalCash = shippingOrderService.findTotalInCome(startDate, endDate);
        List<Integer> customerInMonthList = customerService.getCustomerListInMonth();
        List<Integer> customerInCashList = customerService.getCustomerListInCash();
        //月结客户平均单价
        double averagePriceMonthly = shippingOrderService.getCustomerAvgPrice(customerInMonthList, startDate, endDate);
        //现金客户平均单价
        double averagePriceCash = shippingOrderService.getCustomerAvgPrice(customerInCashList, startDate, endDate);

        return summaryInfoVO;
    }

    @Override
    public SummaryProductInfoVO getSummaryProductInfo() {
        return null;
    }
}
