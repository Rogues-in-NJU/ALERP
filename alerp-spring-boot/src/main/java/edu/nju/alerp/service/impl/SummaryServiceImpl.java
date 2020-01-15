package edu.nju.alerp.service.impl;

import edu.nju.alerp.dto.SummaryInfoDTO;
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
    public SummaryInfoVO getSummaryInfo(SummaryInfoDTO dto) {
        String startDate = dto.getStartTime();
        String endDate = dto.getEndTime();
        // 生成出货单数
        int shippingOrderTotalNum = shippingOrderService.findTotalNum(startDate, endDate);

        int processingOrderTotalNum = processOrderService.queryTotalNum(startDate, endDate);
        // 新增出货单金额
        Double shippingOrderTotalCash = shippingOrderService.findTotalInCome(startDate, endDate);
        List<Integer> customerInMonthList = customerService.getCustomerListInMonth();
        List<Integer> customerInCashList = customerService.getCustomerListInCash();
        //月结客户平均单价
        double averagePriceMonthly = shippingOrderService.getCustomerAvgPrice(customerInMonthList, startDate, endDate);
        //现金客户平均单价
        double averagePriceCash = shippingOrderService.getCustomerAvgPrice(customerInCashList, startDate, endDate);

        SummaryInfoVO summaryInfoVO = SummaryInfoVO.builder().
            totalReceivedCash(arrearOrderService.queryTotalReceivedCash(startDate, endDate)).
            totalOverdueCash(arrearOrderService.queryTotalOverdueCash(startDate, endDate)).
            processingOrderTotalNum(processingOrderTotalNum).
            processingOrderTotalWeight(processOrderService.queryTotalWeight(startDate, endDate)).
            purchaseOrderTotalUnpaidCash(purchaseOrderService.queryUnPaidCash(startDate, endDate)).
            shippingOrderTotalWeight(shippingOrderService.queryTotalWeight(startDate, endDate)).
            shippingOrderTotalNum(shippingOrderTotalNum).
            shippingOrderTotalCash(shippingOrderTotalCash).
            averagePriceMonthly(averagePriceMonthly).
            averagePriceCash(averagePriceCash).
            build();

        return summaryInfoVO;
    }

    @Override
    public SummaryProductInfoVO getSummaryProductInfo() {
        return null;
    }
}
