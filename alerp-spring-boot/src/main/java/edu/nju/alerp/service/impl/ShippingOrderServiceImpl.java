package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.ShippingOrderDTO;
import edu.nju.alerp.entity.ProcessingOrder;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.entity.ShippingOrderProduct;
import edu.nju.alerp.enums.*;
import edu.nju.alerp.repo.CustomerRepository;
import edu.nju.alerp.repo.ProcessingOrderRepository;
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
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    ProcessingOrderRepository processingOrderRepository;
    @Resource
    private DocumentsIdFactory documentsIdFactory;

    /**
     * 生成出货单实体类
     * @param shippingOrderDTO
     * @return
     */
    @Override
    public ShippingOrder addShippingOrder(ShippingOrderDTO shippingOrderDTO) {
        int userId = CommonUtils.getUserId();
        ShippingOrder shippingOrder = ShippingOrder.builder()
                .code(documentsIdFactory.generateNextCode(DocumentsType.SHIPPING_ORDER, CityEnum.of(CommonUtils.getCity())))
                .createdAt(DateUtils.getTodayAccurateToMinute())
                .createdBy(userId)
                .city(CommonUtils.getCity())
                .updatedAt(DateUtils.getTodayAccurateToMinute())
                .updatedBy(userId)
                .status(ShippingOrderStatus.SHIPPIED.getCode())
                .build();
        BeanUtils.copyProperties(shippingOrderDTO, shippingOrder);
        return shippingOrder;
    }

    /**
     * 根据出货单id获取出货单信息
     * @param id
     * @return
     */
    @Override
    public ShippingOrder getShippingOrder(int id) {
        return shippingOrderRepository.getOne(id);
    }

    /**
     * 对外提供的收款单实体类接口
     * @param shippingOrder
     * @return
     */
    @Override
    public int saveShippingOrder(ShippingOrder shippingOrder) {
        if (shippingOrder.getId() == null) {
            return shippingOrderRepository.saveAndFlush(shippingOrder).getId();
        }
        return shippingOrderRepository.save(shippingOrder).getId();

    }

    /**
     * 储存出货单商品
     * @param shippingOrderProduct
     * @return
     */
    @Override
    public int saveShippingOrderProduct(ShippingOrderProduct shippingOrderProduct) {
        if (shippingOrderProduct.getId() == null) {
            return shippingOrderProductRepository.saveAndFlush(shippingOrderProduct).getId();
        } else {
            return shippingOrderProductRepository.save(shippingOrderProduct).getId();
        }
    }

    /**
     * 废弃出货单
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public int deleteShippingOrder(int id) throws Exception {
        ShippingOrder shippingOrder = shippingOrderRepository.getOne(id);
        if (shippingOrder == null) {
            log.error("shippingOrder is null");
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "出货单id不存在！");
        }
        if (shippingOrder.getStatus() == ShippingOrderStatus.ABANDONED.getCode()) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "删除出货单失败，出货单已被删除！");
        }
        shippingOrder.setStatus(ShippingOrderStatus.ABANDONED.getCode());
        shippingOrder.setDeletedAt(DateUtils.getTodayAccurateToMinute());
        shippingOrder.setDeletedBy(CommonUtils.getUserId());
        List<ProcessingOrder> processingOrderList = findProcessingsByShipppingId(shippingOrder.getId());
        processingOrderList.forEach(p -> {
            p.setStatus(ProcessingOrderStatus.UNFINISHED.getCode());
            p.setShippingOrderId(0);
            processingOrderRepository.save(p);
        });
        List<ShippingOrderProduct> shippingOrderProductList = shippingOrderProductRepository.findAllByShippingOrderId(id);
        shippingOrderProductList.forEach(s -> {
            s.setDeletedAt(DateUtils.getTodayAccurateToMinute());
            shippingOrderProductRepository.save(s);
        });
        return shippingOrderRepository.save(shippingOrder).getId();
    }

    // fixme: 及时清除废弃代码

    @Override
    public boolean printShippingOrder(int id) {
        return false;
    }

    /**
     * 根据时间获取出货单列表
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<ShippingOrder> getShippingOrderListByTime(String startTime, String endTime) {
        QueryContainer<ShippingOrder> sp = new QueryContainer<>();
        try {
            if (!"".equals(startTime)) {
                sp.add(ConditionFactory.greatThanEqualTo("createdAt", startTime));
            }
            if (!"".equals(startTime)) {
                sp.add(ConditionFactory.lessThanEqualTo("createdAt", endTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sp.isEmpty()) {
            return shippingOrderRepository.findAll();
        } else {
            return shippingOrderRepository.findAll(sp);
        }
    }

    /**
     * 分页查询出货单列表（流水号，名称，状态，时间）
     * @param pageable
     * @param code
     * @param name
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Page<ShippingOrder> getShippingOrderList(Pageable pageable, String code, String name, Integer status, String startTime, String endTime) {
        QueryContainer<ShippingOrder> sp = new QueryContainer<>();
        try {
            if (status != null) {
                sp.add(ConditionFactory.equal("status", status));
            }
            sp.add(ConditionFactory.equal("city", CommonUtils.getCity()));
            List<Condition> fuzzyMatch = new ArrayList<>();
            if (!"".equals(code)) {
                fuzzyMatch.add(ConditionFactory.like("code", code));
            }
            if (!"".equals(name)) {
                List<Integer> customerIdList = customerRepository.findCustomerIdByNameAndShorthand(name);
                fuzzyMatch.add(ConditionFactory.In("customerId", customerIdList));
            }
            if (!"".equals(startTime)) {
                fuzzyMatch.add(ConditionFactory.greatThanEqualTo("createdAt", startTime));
            }
            if (!"".equals(endTime)) {
                fuzzyMatch.add(ConditionFactory.lessThanEqualTo("createdAt", endTime));
            }
            if (!fuzzyMatch.isEmpty()) {
                sp.add(ConditionFactory.or(fuzzyMatch));
            }
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        return shippingOrderRepository.findAll(sp, pageable);
    }

    /**
     * 根据出货单id获取对应出货单的商品
     * @param shippingOrderId
     * @return
     */
    @Override
    public List<ShippingOrderProduct> getShippingOrderProductList(int shippingOrderId) {
        return shippingOrderProductRepository.findAllByShippingOrderId(shippingOrderId);
    }

    /**
     * 根据出货单id获取对应加工单id
     * @param id
     * @return
     */
    @Override
    public List<Integer> getProcessingListById(int id) {
        return shippingOrderProductRepository.findProcessingListByShippingId(id);
    }

    /**
     * 根据商品id获取该商品总出货金额
     * @param productId
     * @return
     */
    @Override
    public Double getTotalCashByProductId(int productId) {
        return shippingOrderProductRepository.getTotalCashByProductId(productId);
    }

    /**
     * 根据商品id获取该商品总出货重量
     * @param productId
     * @return
     */
    @Override
    public Double getTotalWeightByProductId(int productId) {
        return shippingOrderProductRepository.getTotalWeightByProductId(productId);
    }

    /**
     * 根据商品id和时间获取对应出货商品列表
     * @param productId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<ShippingOrderProduct> getShippingOrderByProductId(int productId, String startTime, String endTime) {
        QueryContainer<ShippingOrderProduct> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("productId", productId));
            sp.add(ConditionFactory.greatThan("expectedWeight", 0));
            sp.add(ConditionFactory.isNull("deletedAt"));
            if ("".equals(startTime))
                sp.add(ConditionFactory.greatThanEqualTo("createAt", startTime));
            if ("".equals(endTime))
                sp.add(ConditionFactory.lessThanEqualTo("createAt", endTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shippingOrderProductRepository.findAll(sp);
    }

    /**
     * controller层根据customerService分别查出现金和月结对客户id List,一起调用该方法返回对应均价
     *
     * @param customerIdList
     * @return
     */
    @Override
    public Double getCustomerAvgPrice(List<Integer> customerIdList, String startDate, String endDate) {
        List<ShippingOrder> shippingOrderList = shippingOrderRepository.findByCustomerList(customerIdList, "", "");
        if (CollectionUtils.isEmpty(shippingOrderList)) {
            return (double) 0;
        }
        double totalCash = shippingOrderList.stream().mapToDouble(ShippingOrder::getReceivableCash).sum();
        double avg = totalCash / shippingOrderList.size();
        BigDecimal b = new BigDecimal(avg);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取总出货重量
     * @param createdAtStartTime
     * @param createdAtEndTime
     * @return
     */
    @Override
    public Double queryTotalWeight(String createdAtStartTime, String createdAtEndTime) {
        QueryContainer<ShippingOrder> shippingSp = new QueryContainer<>();
        QueryContainer<ShippingOrderProduct> productSp = new QueryContainer<>();
        double totalWeight = 0;
        try {
            if (createdAtStartTime != null)
                shippingSp.add(ConditionFactory.greatThanEqualTo("createdAt", createdAtStartTime));
            if (createdAtEndTime != null)
                shippingSp.add(ConditionFactory.lessThanEqualTo("createdAt", createdAtEndTime));
            shippingSp.add(ConditionFactory.notEqual("status", ShippingOrderStatus.ABANDONED.getCode()));
            List<ShippingOrder> shippingOrders = shippingOrderRepository.findAll(shippingSp);
            List<Integer> shippingOrderIds = shippingOrders.parallelStream()
                    .map(ShippingOrder::getId)
                    .collect(Collectors.toList());

            productSp.add(ConditionFactory.In("shippingOrderId", shippingOrderIds));
            productSp.add(ConditionFactory.isNull("deletedAt"));
            List<ShippingOrderProduct> shippingOrderProducts = shippingOrderProductRepository.findAll(productSp);
            totalWeight = shippingOrderProducts.parallelStream()
                    .mapToDouble(ShippingOrderProduct::getWeight).sum();
        } catch (Exception e) {
            log.error("Value is null.", e);
        }
        return totalWeight;
    }

    @Override
    public Integer findTotalNum(String startDate, String endDate) {
        return shippingOrderRepository.findTotalNum(startDate, endDate);
    }

    @Override
    public Double findTotalInCome(String startDate, String endDate) {
        return shippingOrderRepository.findTotalInCome(startDate, endDate);
    }

    @Override
    public ShippingOrder getShippingOrderByArrearOrderId(int arrearOrderId) {
        ShippingOrder record = shippingOrderRepository.findShippingOrderByArrearOrderId(arrearOrderId);
        if (record == null) {
            log.error("根据收款单id没找到对应的出货单");
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "根据收款单id没找到对应的出货单");
        }
        return record;
    }

    private List<ProcessingOrder> findProcessingsByShipppingId(int id) {
        QueryContainer<ProcessingOrder> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("shippingOrderId", id));
        } catch (Exception e) {
            log.error("Value is null.", e);
        }
        return processingOrderRepository.findAll(sp);
    }
}
