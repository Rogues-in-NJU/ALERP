package edu.nju.alerp.service;

import edu.nju.alerp.dto.ShippingOrderDTO;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.entity.ShippingOrderProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description: 出货单服务层接口
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:42
 */
public interface ShippingOrderService {
    int addShippingOrder(ShippingOrderDTO shippingOrderDTO);

    ShippingOrder getShippingOrder(int id);

    int deleteShippingOrder(int id);

    boolean printShippingOrder(int id);

    Page<ShippingOrder> getShippingOrderList(Pageable pageable, String code, String name, Integer status, String startTime, String endTime);

    List<ShippingOrderProduct> getShippingOrderProductList(int shippingOrderId);

    List<Integer> getProcessingListById(int id);

    Double getTotalCashByProductId(int productId);

    Double getTotalWeightByProductId(int productId);

    Double getCustomerAvgPrice(List<Integer> customerIdList);

    Double queryTotalWeight(String createdAtStartTime, String createdAtEndTime);

    /**
     * 根据收款单id找到一条出货单记录
     * @param arrearOrderId
     * @return
     */
    ShippingOrder getShippingOrderByArrearOrderId(int arrearOrderId);
}