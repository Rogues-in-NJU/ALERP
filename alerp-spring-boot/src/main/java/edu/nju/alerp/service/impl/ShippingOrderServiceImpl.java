package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.ShippingOrderDTO;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.entity.ShippingOrderProduct;
import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.ShippingOrderStatus;
import edu.nju.alerp.repo.CustomerRepository;
import edu.nju.alerp.repo.ShippingOrderProductRepository;
import edu.nju.alerp.repo.ShippingOrderRepository;
import edu.nju.alerp.service.ShippingOrderService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    CustomerRepository customerRepository;
    @Autowired
    ShippingOrderProductRepository shippingOrderProductRepository;
    @Resource
    private DocumentsIdFactory documentsIdFactory;

    @Override
    public int addShippingOrder(ShippingOrderDTO shippingOrderDTO) {
        int userId = CommonUtils.getUserId();
        ShippingOrder shippingOrder = ShippingOrder.builder()
                .code(documentsIdFactory.generateNextCode(DocumentsType.SHIPPING_ORDER, CityEnum.of(CommonUtils.getCity())))
                .createdAt(DateUtils.getToday())
                .createdBy(userId)
                .city(CommonUtils.getCity())
                .updatedAt(DateUtils.getToday())
                .updatedBy(userId)
                .status(ShippingOrderStatus.SHIPPIED.getCode())
                .build();
        BeanUtils.copyProperties(shippingOrderDTO, shippingOrder);
        shippingOrderDTO.getProducts().forEach(p -> {
            ShippingOrderProduct shippingOrderProduct = ShippingOrderProduct.builder().build();
            BeanUtils.copyProperties(p, shippingOrderProduct);
            shippingOrderProductRepository.save(shippingOrderProduct);
        });
        return shippingOrderRepository.saveAndFlush(shippingOrder).getId();
    }

    @Override
    public ShippingOrder getShippingOrder(int id) {
        return shippingOrderRepository.getOne(id);
    }

    @Override
    public int deleteShippingOrder(int id) {
        ShippingOrder shippingOrder = shippingOrderRepository.getOne(id);
        if (shippingOrder == null) {
            log.error("shippingOrder is null");
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "出货单id不存在！");
        }
        if (shippingOrder.getStatus() == ShippingOrderStatus.ABANDONED.getCode()) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "删除出货单失败，出货单已被删除！");
        }
        shippingOrder.setStatus(ShippingOrderStatus.ABANDONED.getCode());
        shippingOrder.setDeletedAt(DateUtils.getToday());
        shippingOrder.setDeletedBy(CommonUtils.getUserId());
        return shippingOrderRepository.save(shippingOrder).getId();
    }

    @Override
    public boolean printShippingOrder(int id) {
        return false;
    }

    @Override
    public Page<ShippingOrder> getShippingOrderList(Pageable pageable, String name, int status, String startTime, String endTime) {
        QueryContainer<ShippingOrder> sp = new QueryContainer<>();
        List<Integer> customerIdList = customerRepository.findCustomerIdByNameAndShorthand(name);
        try {
            sp.add(ConditionFactory.equal("status", status));
            sp.add(ConditionFactory.equal("city", CommonUtils.getCity()));
            sp.add(ConditionFactory.In("customerId", customerIdList));
            List<Condition> fuzzyMatch = new ArrayList<>();
            if (!"".equals(name)) {
                fuzzyMatch.add(ConditionFactory.like("name", name));
                fuzzyMatch.add(ConditionFactory.like("shorthand", name));
            }
            fuzzyMatch.add(ConditionFactory.greatThanEqualTo("createdAt", startTime));
            fuzzyMatch.add(ConditionFactory.lessThanEqualTo("createdAt", endTime));
            sp.add(ConditionFactory.or(fuzzyMatch));
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        return shippingOrderRepository.findAll(sp, pageable);
    }

    @Override
    public List<ShippingOrderProduct> getShippingOrderProductList(int shippingOrderId) {
        return shippingOrderProductRepository.findAllByShippingOrderId(shippingOrderId);
    }

    @Override
    public List<Integer> getProcessingListById(int id) {
        return shippingOrderProductRepository.findProcessingListByShippingId(id);
    }
}
