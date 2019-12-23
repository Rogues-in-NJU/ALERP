package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.ShippingOrderDTO;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.enums.ShippingOrderStatus;
import edu.nju.alerp.repo.ReceiptRecordRepository;
import edu.nju.alerp.repo.ShippingOrderRepository;
import edu.nju.alerp.service.ShippingOrderService;
import edu.nju.alerp.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 出货单服务层接口实现
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:45
 */
@Slf4j
@Service
public class ShippingOrderServiceImpl implements ShippingOrderService {
    @Autowired
    ShippingOrderRepository shippingOrderRepository;
    @Autowired
    ReceiptRecordRepository receiptRecordRepository;

    @Override
    public int addShippingOrder(ShippingOrderDTO shippingOrderDTO) {
        ShippingOrder shippingOrder = ShippingOrder.builder()
//                .code()
                .createdAt(DateUtils.getToday())
                .status(ShippingOrderStatus.SHIPPIED.getCode())
                .build();
        BeanUtils.copyProperties(shippingOrderDTO, shippingOrder);
        int res = shippingOrderRepository.saveAndFlush(shippingOrder).getId();
        //todo 生成收款单
        return res;
    }

    @Override
    public int ShippingOrderInfo(int id) {
        return 0;
    }

    @Override
    public boolean deleteShippingOrder(int id) {
        ShippingOrder shippingOrder = shippingOrderRepository.getOne(id);
        if (shippingOrder == null) {
            log.error("shippingOrder is null");
            return false;
        }
        shippingOrder.setStatus(ShippingOrderStatus.ABANDONED.getCode());
        shippingOrderRepository.save(shippingOrder);
        return true;
    }

    @Override
    public boolean printShippingOrder(int id) {
        return false;
    }

    @Override
    public Page<ShippingOrder> getShippingOrderList(Pageable pageable, String customerName, int status, String startTime, String endTime) {
        QueryContainer<ShippingOrder> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("status", status));
            List<Condition> fuzzyMatch = new ArrayList<>();
            fuzzyMatch.add(ConditionFactory.like("name", customerName));
            fuzzyMatch.add(ConditionFactory.like("shorthand", customerName));
            fuzzyMatch.add(ConditionFactory.greatThanEqualTo("created_at",startTime));
            fuzzyMatch.add(ConditionFactory.lessThanEqualTo("created_at",endTime));
            sp.add(ConditionFactory.or(fuzzyMatch));
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        return shippingOrderRepository.findAll(sp, pageable);
    }
}
