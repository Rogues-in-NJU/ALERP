package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.ProcessingOrderDTO;
import edu.nju.alerp.dto.UpdateProcessProductDTO;
import edu.nju.alerp.entity.*;
import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.ProcessingOrderStatus;
import edu.nju.alerp.repo.CustomerRepository;
import edu.nju.alerp.repo.ProcessOrderProductRepository;
import edu.nju.alerp.repo.ProcessingOrderRepository;
import edu.nju.alerp.service.CustomerService;
import edu.nju.alerp.service.ProcessOrderService;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.vo.ProcessingOrderDetailVO;
import edu.nju.alerp.vo.ProcessingOrderListVO;
import edu.nju.alerp.vo.ProcessingOrderProductVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerRepository customerRepository;

    @Resource
    private DocumentsIdFactory documentsIdFactory;

    @Override
    public List<ProcessingOrder> findAll() {
        return processingOrderRepository.findAll();
    }

    @Override
    public ProcessingOrderDetailVO findProcessingById(int id) {
        ProcessingOrder processingOrder = processingOrderRepository.getOne(id);
        Customer customerForProcessingOrder = customerService.getCustomer(processingOrder.getCustomerId());
        QueryContainer<ProcessOrderProduct> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("processOrderId", id));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        List<ProcessOrderProduct> pops = processOrderProductRepository.findAll(sp);
        List<SpecialPrice> specialPrices =  customerService.getSpecialPricesListByCustomerId(processingOrder.getCustomerId());
        Map<Integer, SpecialPrice> specialPriceMap = specialPrices.parallelStream()
                .map(specialPrice -> MutablePair.of(specialPrice.getProductId(), specialPrice))
                .collect(Collectors.toMap(MutablePair::getLeft, MutablePair::getRight));

        List<ProcessingOrderProductVO> productVOS = pops.parallelStream().map(processOrderProduct -> generateProcessingOrderProductVO(processOrderProduct, specialPriceMap))
                                                                        .filter(Objects::nonNull)
                                                                        .collect(Collectors.toList());
        double totalWeight = productVOS.parallelStream().mapToDouble(ProcessingOrderProductVO::getExpectedWeight).sum();

        return ProcessingOrderDetailVO.builder().id(processingOrder.getId())
                                                .code(processingOrder.getCode())
                                                .customerId(processingOrder.getCustomerId())
                                                .customerName(customerForProcessingOrder.getName())
                                                .shippingOrderId(processingOrder.getShippingOrderId())
                                                .salesman(processingOrder.getSalesman())
                                                .status(processingOrder.getStatus())
                                                .createdAt(processingOrder.getCreateAt())
                                                .createdById(String.valueOf(processingOrder.getCreateBy()))
                                                .createdByName(userService.getUser(processingOrder.getCreateBy()).getName())
                                                .updatedAt(processingOrder.getUpdateAt())
                                                .totalWeight(totalWeight)
                                                .products(productVOS).build();
    }

    @Override
    public ProcessingOrder getOne(int id) {
        return processingOrderRepository.getOne(id);
    }

    @Override
    public int savaProcessingOrder(ProcessingOrder processingOrder) {
        return processingOrderRepository.saveAndFlush(processingOrder).getId();
    }

    @Override
    public int addProcessingOrder(ProcessingOrderDTO processingOrderDTO) {
        String city = CityEnum.of(CommonUtils.getCity()).getMessage();
        ProcessingOrder processingOrder = ProcessingOrder.builder()
                                        .code(documentsIdFactory.generateNextCode(DocumentsType.PROCESSING_ORDER, CityEnum.of(CommonUtils.getCity())))
                                        .status(ProcessingOrderStatus.DRAFTING.getCode())
                                        .customerId(processingOrderDTO.getCustomerId())
                                        .salesman(processingOrderDTO.getSalesman())
                                        .city(city)
                                        .createAt(DateUtils.getToday())
                                        .createBy(CommonUtils.getUserId())
                                        .updateAt(DateUtils.getToday())
                                        .updateBy(CommonUtils.getUserId())
                                        .build();

        ProcessingOrder entity = processingOrderRepository.saveAndFlush(processingOrder);
        List<ProcessOrderProduct> processOrderProducts =
                processingOrderDTO.getProducts().parallelStream()
                                                .map(p -> ProcessOrderProduct.builder()
                                                            .productId(p.getProductId())
                                                            .specification(p.getSpecification())
                                                            .quantity(p.getQuantity())
                                                            .processOrderId(entity.getId())
                                                            .expectedWeight(p.getExpectedWeight()).build())
                                                .collect(Collectors.toList());
        processOrderProductRepository.saveAll(processOrderProducts);
        processOrderProductRepository.flush();
        return entity.getId();
    }

    @Override
    public List<ProcessingOrder> findProcessingsByShipppingId(int id) {
        QueryContainer<ProcessingOrder> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("shippingOrderId", id));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        return processingOrderRepository.findAll(sp);
    }

    @Override
    public double queryTotalWeight(String createdAtStartTime, String createdAtEndTime) {
        QueryContainer<ProcessingOrder> processSp = new QueryContainer<>();
        QueryContainer<ProcessOrderProduct> productSp = new QueryContainer<>();
        double totalWeight = 0;
        try{
            if (createdAtStartTime != null)
                processSp.add(ConditionFactory.greatThanEqualTo("createAt", createdAtStartTime));
            if (createdAtEndTime != null)
                processSp.add(ConditionFactory.lessThanEqualTo("createAt", createdAtEndTime));
            processSp.add(ConditionFactory.notEqual("status", ProcessingOrderStatus.ABANDONED.getCode()));
            List<ProcessingOrder> processingOrders = processingOrderRepository.findAll(processSp);
            List<Integer> processOrderIds = processingOrders.parallelStream()
                                                        .map(ProcessingOrder::getId)
                                                        .collect(Collectors.toList());

            productSp.add(ConditionFactory.In("processOrderId", processOrderIds));
            List<ProcessOrderProduct> processOrderProducts = processOrderProductRepository.findAll(productSp);
            totalWeight = processOrderProducts.parallelStream()
                                                .mapToDouble(ProcessOrderProduct::getExpectedWeight)
                                                .sum();
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        return totalWeight;
    }

    @Override
    public int addOrUpdateProcessProduct(UpdateProcessProductDTO updateProcessProductDTO) {
        ProcessingOrder processingOrder = processingOrderRepository.getOne(updateProcessProductDTO.getProcessingOrderId());
        if (!updateProcessProductDTO.getProcessingOrderUpdatedAt().equals(processingOrder.getUpdateAt())) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "加工单信息已变更，请重新更新");
        }
        ProcessOrderProduct processOrderProduct = ProcessOrderProduct.builder()
                                                                    .processOrderId(updateProcessProductDTO.getProcessingOrderId())
                                                                    .productId(updateProcessProductDTO.getProductId())
                                                                    .quantity(updateProcessProductDTO.getQuantity())
                                                                    .specification(updateProcessProductDTO.getSpecification())
                                                                    .expectedWeight(updateProcessProductDTO.getExpectedWeight())
                                                                    .build();

        if (updateProcessProductDTO.getId() != null) {
            processOrderProduct = processOrderProductRepository.getOne(updateProcessProductDTO.getId());
            processOrderProduct.setProcessOrderId(updateProcessProductDTO.getProcessingOrderId());
            processOrderProduct.setProductId(updateProcessProductDTO.getProductId());
            processOrderProduct.setSpecification(updateProcessProductDTO.getSpecification());
            processOrderProduct.setQuantity(updateProcessProductDTO.getQuantity());
            processOrderProduct.setExpectedWeight(updateProcessProductDTO.getExpectedWeight());
        }

        processingOrder.setUpdateAt(DateUtils.getToday());
        processingOrder.setUpdateBy(CommonUtils.getUserId());
        processOrderProduct = processOrderProductRepository.saveAndFlush(processOrderProduct);
        processingOrderRepository.saveAndFlush(processingOrder);
        return processOrderProduct.getId();
    }

    @Override
    public int deleteProcessProduct(int id) throws Exception{
        ProcessOrderProduct processOrderProduct = processOrderProductRepository.getOne(id);
        if (processOrderProduct == null)
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "该条目不存在");
        ProcessingOrder processingOrder = processingOrderRepository.getOne(processOrderProduct.getProcessOrderId());
        if (processingOrder == null)
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "不存在该加工单");
        if (ProcessingOrderStatus.of(processingOrder.getStatus()) != ProcessingOrderStatus.DRAFTING)
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "该状态下单据不支持该操作");
        processOrderProductRepository.deleteById(id);
        return id;
    }

    @Override
    public int printProcessingOrder(int id) throws Exception{
        ProcessingOrder processingOrder = processingOrderRepository.getOne(id);
        if (processingOrder == null)
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "不存在该加工单");
        if (!ProcessingOrderStatus.of(processingOrder.getStatus()).printable())
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "该状态下单据不支持该操作");
        processingOrder.setStatus(ProcessingOrderStatus.UNFINISHED.getCode());
        processingOrderRepository.saveAndFlush(processingOrder);
        return id;
    }

    @Override
    public int abandonProcessingOrder(int id) throws Exception{
        ProcessingOrder processingOrder = processingOrderRepository.getOne(id);
        if (processingOrder == null)
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "不存在该加工单");
        if (!ProcessingOrderStatus.of(processingOrder.getStatus()).abandonable())
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "该状态下单据不支持该操作");
        processingOrder.setStatus(ProcessingOrderStatus.ABANDONED.getCode());
        processingOrder.setDeleteAt(DateUtils.getToday());
        processingOrder.setDeleteBy(CommonUtils.getUserId());
        processingOrderRepository.saveAndFlush(processingOrder);
        return id;
    }

    @Override
    public Page<ProcessingOrderListVO> findAllByPage(Pageable pageable, String id, String customerName,
                                                     Integer status, String createAtStartTime, String createAtEndTime) {
        QueryContainer<ProcessingOrder> sp = new QueryContainer<>();
        String city = CityEnum.of(CommonUtils.getCity()).getMessage();
        List<Integer> customers = new ArrayList<>();
        if (customerName != null)
            customers = customerRepository.findCustomerIdByNameAndShorthand(customerName);
        try {
            if (customerName != null)
                sp.add(ConditionFactory.In("customerId",customers));
            if (id != null)
                sp.add(ConditionFactory.like("code", id));
            if (status != null)
                sp.add(ConditionFactory.equal("status", status));
            else
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "status"));
//            sp.add(ConditionFactory.between("createAt", createAtStartTime, createAtEndTime));
            if (createAtStartTime != null)
                sp.add(ConditionFactory.greatThanEqualTo("createAt", createAtStartTime));
            if (createAtEndTime != null)
                sp.add(ConditionFactory.lessThanEqualTo("createAt", createAtEndTime));
            sp.add(ConditionFactory.equal("city", city));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        Page<ProcessingOrder> processingOrderPage = processingOrderRepository.findAll(sp, pageable);

        List<ProcessingOrderListVO> result = processingOrderPage.getContent()
                .stream().map(processingOrder ->
                        ProcessingOrderListVO.buildProcessingOrderListVO(processingOrder,
                                customerService.getCustomer(processingOrder.getCustomerId()).getName(),userService.getUser(processingOrder.getCreateBy()).getName()))
                .collect(Collectors.toList());
        return new PageImpl<>(result, pageable, processingOrderPage.getTotalElements());
    }


    private ProcessingOrderProductVO generateProcessingOrderProductVO(ProcessOrderProduct processOrderProduct,
                                                                      Map<Integer, SpecialPrice> specialPriceMap) {
        Product pro = productService.findProductById(processOrderProduct.getProductId());
        if (pro == null)
            return null;

        SpecialPrice specialPrice = specialPriceMap.get(pro.getId());
        ProcessingOrderProductVO processingOrderProductVO;
        if (specialPrice == null) {
            processingOrderProductVO = ProcessingOrderProductVO.builder().id(processOrderProduct.getId())
                    .productId(pro.getId())
                    .productName(pro.getName())
                    .type(pro.getType())
                    .density(pro.getDensity())
                    .productSpecification(pro.getSpecification())
                    .specification(processOrderProduct.getSpecification())
                    .quantity(processOrderProduct.getQuantity())
                    .expectedWeight(processOrderProduct.getExpectedWeight())
                    .isEditable(true).build();
        }else {
            processingOrderProductVO = ProcessingOrderProductVO.builder().id(processOrderProduct.getId())
                    .productId(pro.getId())
                    .productName(pro.getName())
                    .type(pro.getType())
                    .density(pro.getDensity())
                    .productSpecification(pro.getSpecification())
                    .specification(processOrderProduct.getSpecification())
                    .quantity(processOrderProduct.getQuantity())
                    .expectedWeight(processOrderProduct.getExpectedWeight())
                    .isEditable(false)
                    .specialPrice(specialPrice.getPrice())
                    .specialPriceType(specialPrice.getPriceType()).build();
        }
        return processingOrderProductVO;
    }
}
