package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.ProcessingOrderDTO;
import edu.nju.alerp.dto.UpdateProcessProductDTO;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.ProcessOrderProduct;
import edu.nju.alerp.entity.ProcessingOrder;
import edu.nju.alerp.entity.Product;
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
import edu.nju.alerp.util.TimeUtil;
import edu.nju.alerp.vo.ProcessingOrderDetailVO;
import edu.nju.alerp.vo.ProcessingOrderProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
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
                                                .customerName(customerForProcessingOrder.getName())
                                                .shippingOrderId(processingOrder.getShippingOrderId())
                                                .salesman(processingOrder.getSalesman())
                                                .status(processingOrder.getStatus())
                                                .createdAt(processingOrder.getCreateAt())
                                                .createdById(String.valueOf(processingOrder.getCreateBy()))
                                                .createdByName(userService.getUser(processingOrder.getCreateBy()).getName())
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
        ProcessingOrder processingOrder = ProcessingOrder.builder()
                                        .code(documentsIdFactory.generateNextCode(DocumentsType.PROCESSING_ORDER))
                                        .status(ProcessingOrderStatus.DRAFTING.getCode())
                                        .customerId(processingOrderDTO.getCustomerId())
                                        .salesman(processingOrderDTO.getSalesman())
                                        .createAt(TimeUtil.dateFormat(new Date()))
                                        .createBy(CommonUtils.getUserId())
                                        .updateAt(TimeUtil.dateFormat(new Date()))
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
    public int addOrUpdateProcessProduct(UpdateProcessProductDTO updateProcessProductDTO) {
        ProcessOrderProduct processOrderProduct = ProcessOrderProduct.builder()
                                                                    .processOrderId(updateProcessProductDTO.getProcessingOrderId())
                                                                    .productId(updateProcessProductDTO.getProductId())
                                                                    .quantity(updateProcessProductDTO.getQuantity())
                                                                    .specification(updateProcessProductDTO.getSpecification())
                                                                    .expectedWeight(updateProcessProductDTO.getExpectedWeight())
                                                                    .build();
        if (updateProcessProductDTO.getId() != null)
            processOrderProduct.setId(updateProcessProductDTO.getId());
        ProcessingOrder processingOrder = ProcessingOrder.builder().id(updateProcessProductDTO.getProductId())
                                                                    .updateAt(TimeUtil.dateFormat(new Date()))
                                                                     .updateBy(CommonUtils.getUserId())
                                                                    .build();
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
        processingOrder.setDeleteAt(TimeUtil.dateFormat(new Date()));
        processingOrder.setDeleteBy(CommonUtils.getUserId());
        processingOrderRepository.saveAndFlush(processingOrder);
        return id;
    }

    @Override
    public Page<ProcessingOrder> findAllByPage(Pageable pageable, String id, String customerName,
                                                   Integer status, String createAtStartTime, String createAtEndTime) {
        QueryContainer<ProcessingOrder> sp = new QueryContainer<>();
        List<Integer> customers = new ArrayList<>();
        if (customerName != null)  // todo 这里要改一下，是不是在这里判空
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
            sp.add(ConditionFactory.greatThanEqualTo("createAt", createAtStartTime));
            sp.add(ConditionFactory.lessThanEqualTo("createAt", createAtEndTime));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        return processingOrderRepository.findAll(sp, pageable);
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
}
