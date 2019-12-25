package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.cache.Cache;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.ProcessingOrderDTO;
import edu.nju.alerp.dto.UpdateProcessProductDTO;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.ProcessOrderProduct;
import edu.nju.alerp.entity.ProcessingOrder;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.enums.ProcessingOrderStatus;
import edu.nju.alerp.enums.ProductType;
import edu.nju.alerp.repo.CustomerRepository;
import edu.nju.alerp.repo.ProcessOrderProductRepository;
import edu.nju.alerp.repo.ProcessingOrderRepository;
import edu.nju.alerp.service.CustomerService;
import edu.nju.alerp.service.ProcessOrderService;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.util.TimeUtil;
import edu.nju.alerp.vo.ProcessingOrderDetailVO;
import edu.nju.alerp.vo.ProcessingOrderProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
                                                .createdByName("从User那里去拿")  //todo
                                                .products(productVOS).build();
    }

    @Override
    public int addProcessingOrder(ProcessingOrderDTO processingOrderDTO) {
        ProcessingOrder processingOrder = ProcessingOrder.builder()
                                        .code(documentsIdFactory.generateNextCode(DocumentsType.PROCESSING_ORDER))
                                        .status(ProcessingOrderStatus.DRAFTING.getCode())
                                        .customerId(processingOrderDTO.getCustomerId())
                                        .salesman(processingOrderDTO.getSalesman())
                                        .createAt(TimeUtil.dateFormat(new Date()))
                                        //.createBy() 要去从session里拿
                                        .updateAt(TimeUtil.dateFormat(new Date()))
                                        //.updateBy() session里拿
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
                                                                    .id(updateProcessProductDTO.getId())
                                                                    .processOrderId(updateProcessProductDTO.getProcessingOrderId())
                                                                    .productId(updateProcessProductDTO.getProductId())
                                                                    .quantity(updateProcessProductDTO.getQuantity())
                                                                    .specification(updateProcessProductDTO.getSpecification())
                                                                    .expectedWeight(updateProcessProductDTO.getExpectedWeight())
                                                                    .build();
        ProcessingOrder processingOrder = ProcessingOrder.builder().id(updateProcessProductDTO.getProductId())
                                                                    .updateAt(TimeUtil.dateFormat(new Date()))
                                                                    // todo .updateBy() session里拿
                                                                    .build();
        processOrderProduct = processOrderProductRepository.saveAndFlush(processOrderProduct);
        processingOrderRepository.saveAndFlush(processingOrder);
        return processOrderProduct.getId();
    }

    @Override
    public int deleteProcessProduct(int id) throws Exception{
        ProcessOrderProduct processOrderProduct = processOrderProductRepository.getOne(id);
        if (processOrderProduct == null)
            return 0; //todo 抛出空异常
        ProcessingOrder processingOrder = processingOrderRepository.getOne(processOrderProduct.getProcessOrderId());
        if (processingOrder == null)
            return 0;
        if (ProcessingOrderStatus.of(processingOrder.getStatus()) != ProcessingOrderStatus.DRAFTING)
            return 0; // todo 抛出该单据不支持删除的异常
        processOrderProductRepository.deleteById(id);
        return id;
    }

    @Override
    public int printProcessingOrder(int id) throws Exception{
        ProcessingOrder processingOrder = processingOrderRepository.getOne(id);
        if (processingOrder == null)
            return 0;  //todo 抛出空异常
        if (!ProcessingOrderStatus.of(processingOrder.getStatus()).printable())
            return 0; // todo 抛出该单据不支持打印的异常
        processingOrder.setStatus(ProcessingOrderStatus.UNFINISHED.getCode());
        processingOrderRepository.saveAndFlush(processingOrder);
        return id;
    }

    @Override
    public int abandonProcessingOrder(int id) throws Exception{
        ProcessingOrder processingOrder = processingOrderRepository.getOne(id);
        if (processingOrder == null)
            return 0;  //todo 抛出空异常
        if (!ProcessingOrderStatus.of(processingOrder.getStatus()).abandonable())
            return 0; // todo 抛出该单据不支持废弃的异常
        processingOrder.setStatus(ProcessingOrderStatus.ABANDONED.getCode());
        processingOrder.setDeleteAt(TimeUtil.dateFormat(new Date()));
        //processingOrder.setDeleteBy();
        processingOrderRepository.saveAndFlush(processingOrder);
        return id;
    }

    @Override
    public Page<ProcessingOrder> findAllByPage(Pageable pageable, String id, String customerName,
                                                   Integer status, String createAtStartTime, String createAtEndTime) {
        QueryContainer<ProcessingOrder> sp = new QueryContainer<>();
        List<Integer> customers = customerRepository.findCustomerIdByNameAndShorthand(customerName);
        try {
            sp.add(ConditionFactory.In("customerId",customers));
            sp.add(ConditionFactory.like("code", id));
            sp.add(ConditionFactory.equal("status", status));
            sp.add(ConditionFactory.between("create_at", createAtStartTime, createAtEndTime));
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
