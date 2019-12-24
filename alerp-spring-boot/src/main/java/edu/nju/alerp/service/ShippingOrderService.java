package edu.nju.alerp.service;

import edu.nju.alerp.dto.ShippingOrderDTO;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @Description: 出货单服务层接口
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:42
 */
public interface ShippingOrderService {
    int addShippingOrder(ShippingOrderDTO shippingOrderDTO);

    int ShippingOrderInfo(int id);

    boolean deleteShippingOrder(int id);

    boolean printShippingOrder(int id);

    Page<ShippingOrder> getShippingOrderList(Pageable pageable, String name, int status, String startTime, String endTime);
}