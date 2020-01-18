package edu.nju.alerp.service.impl;

import edu.nju.alerp.dto.SummaryInfoDTO;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.entity.ShippingOrderProduct;
import edu.nju.alerp.service.*;
import edu.nju.alerp.vo.SummaryInfoVO;
import edu.nju.alerp.vo.SummaryProductInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ProductService productService;

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
    public List<SummaryProductInfoVO> getSummaryProductInfo(String name, String startTime, String endTime) {
        List<SummaryProductInfoVO> summaryProductInfoVOList = new ArrayList<>();
        List<ShippingOrder> shippingOrderList = shippingOrderService.getShippingOrderListByTime(startTime, endTime);
        List<ShippingOrderProduct> shippingOrderProductList = new ArrayList<>();
        shippingOrderList.forEach(s -> {
            shippingOrderProductList.addAll(shippingOrderService.getShippingOrderProductList(s.getId()));
        });
        List<Integer> productIdList = shippingOrderProductList.stream().map(ShippingOrderProduct::getProductId).collect(Collectors.toList());
        // id去重
        HashSet set = new HashSet(productIdList);
        productIdList.clear();
        productIdList.addAll(set);
        // 过滤name
        if (!"".equals(name)) {
            productIdList = productIdList.stream()
                    .filter(p -> productService.findProductNameById(p).contains(name))
                    .collect(Collectors.toList());
        }

        productIdList.forEach(p -> {
            List<ShippingOrderProduct> subShippingProductList = shippingOrderProductList.stream()
                    .filter(s -> s.getProductId() == p)
                    .collect(Collectors.toList());
            shippingOrderProductList.removeAll(subShippingProductList);
            int size = subShippingProductList.size();
            double weight = subShippingProductList.stream().mapToDouble(ShippingOrderProduct::getWeight).sum();
            double totalCash = subShippingProductList.stream().mapToDouble(ShippingOrderProduct::getCash).sum();
            double avg = totalCash / size;
            BigDecimal b = new BigDecimal(avg);
            SummaryProductInfoVO summaryProductInfoVO = SummaryProductInfoVO.builder()
                    .id(p)
                    .name(productService.findProductNameById(p))
                    .totalWeight(weight)
                    .averagePrice(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue())
                    .build();
            summaryProductInfoVOList.add(summaryProductInfoVO);
        });
        summaryProductInfoVOList.sort(Comparator.comparing(SummaryProductInfoVO::getId));
        return summaryProductInfoVOList;
    }
}
