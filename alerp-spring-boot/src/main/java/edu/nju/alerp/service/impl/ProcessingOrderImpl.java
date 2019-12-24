package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.entity.ProcessOrderProduct;
import edu.nju.alerp.entity.ProcessingOrder;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.repo.ProcessOrderProductRepository;
import edu.nju.alerp.repo.ProcessingOrderRepository;
import edu.nju.alerp.service.ProcessOrderService;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.vo.ProcessingOrderDetailVO;
import edu.nju.alerp.vo.ProcessingOrderProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 加工单服务实现类
 * @Author: yangguan
 * @CreateDate: 2019-12-24 16:01
 */
@Slf4j
@Service
public class ProcessingOrderImpl implements ProcessOrderService {

    @Autowired
    private ProcessingOrderRepository processingOrderRepository;

    @Autowired
    private ProcessOrderProductRepository processOrderProductRepository;

    @Autowired
    private ProductService productService;

    @Override
    public List<ProcessingOrder> findAll() {
        return processingOrderRepository.findAll();
    }

    @Override
    public ProcessingOrderDetailVO findProcessingById(int id) {
        ProcessingOrder processingOrder = processingOrderRepository.getOne(id);
        QueryContainer<ProcessOrderProduct> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("process_order_id", id));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        List<ProcessOrderProduct> pops = processOrderProductRepository.findAll(sp);
        List<ProcessingOrderProductVO> productVOS = pops.parallelStream().map(this::generateProcessingOrderProductVO)
                                                                        .filter(Objects::nonNull)
                                                                        .collect(Collectors.toList());

        return ProcessingOrderDetailVO.builder().id(processingOrder.getId())
                                                .code(processingOrder.getCode())
                                                .customerId(processingOrder.getCustomerId())
                                                .customerName(processingOrder.getCustomerName())
                                                .shippingOrderId(processingOrder.getShippingOrderId())
                                                .salesman(processingOrder.getSalesman())
                                                .status(processingOrder.getStatus())
                                                .createdAt(processingOrder.getCreateAt())
                                                .createdById(String.valueOf(processingOrder.getCreateBy()))
                                                .createdByName("从customer那里去拿")
                                                .products(productVOS).build();
    }

    private ProcessingOrderProductVO generateProcessingOrderProductVO(ProcessOrderProduct processOrderProduct) {
        Product pro = productService.findProductById(processOrderProduct.getProductId());
        if (pro == null)
            return null;
        return ProcessingOrderProductVO.builder().id(processOrderProduct.getId())
                                                .productId(pro.getId())
                                                .productName(pro.getName())
                                                .type(pro.getType())
                                                .density(pro.getDensity())
                                                .productSpecification(pro.getSpecification())
                                                .specification(processOrderProduct.getSpecification())
                                                .quantity(processOrderProduct.getQuantity())
                                                .expectedWeight(processOrderProduct.getExpectedWeight()).build();
    }

    @Override
    public Page<ProcessingOrder> findAllByPage(Pageable pageable, String id, String customerName,
                                                   Integer status, String createAtStartTime, String createAtEndTime) {
        QueryContainer<ProcessingOrder> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.like("code", id));
            List<Condition> fuzzyMatch = new ArrayList<>();
            fuzzyMatch.add(ConditionFactory.like("customer_name", customerName));
            fuzzyMatch.add(ConditionFactory.like("shorthand", customerName));
            sp.add(ConditionFactory.or(fuzzyMatch));
            sp.add(ConditionFactory.equal("status", status));
            sp.add(ConditionFactory.between("create_at", createAtStartTime, createAtEndTime));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        return processingOrderRepository.findAll(sp, pageable);
    }
}
