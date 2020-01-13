package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.AddPaymentRecordDTO;
import edu.nju.alerp.dto.PurchaseOrderDTO;
import edu.nju.alerp.entity.PaymentRecord;
import edu.nju.alerp.entity.ProcessOrderProduct;
import edu.nju.alerp.entity.PurchaseOrder;
import edu.nju.alerp.entity.PurchaseOrderProduct;
import edu.nju.alerp.enums.*;
import edu.nju.alerp.repo.PaymentRecordRepository;
import edu.nju.alerp.repo.PurchaseOrderProductRepository;
import edu.nju.alerp.repo.PurchaseOrderRepository;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.service.PurchaseOrderService;
import edu.nju.alerp.service.SupplierService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.vo.PurchaseOrderDetailVO;
import edu.nju.alerp.vo.PurchaseOrderListVO;
import edu.nju.alerp.vo.PurchaseProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 采购单服务接口实现类
 * @Author: yangguan
 * @CreateDate: 2019-12-23 21:28
 */
@Service
@Slf4j
public class PurchaseOrderImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderProductRepository purchaseOrderProductRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private DocumentsIdFactory documentsIdFactory;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Override
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Page<PurchaseOrderListVO> findAllByPage(Pageable pageable, String id, Integer status, String doneStartTime, String doneEndTime) {
        String city = CityEnum.of(CommonUtils.getCity()).getMessage();
        QueryContainer<PurchaseOrder> sp = new QueryContainer<>();
        try {
            if (id != null)
                sp.add(ConditionFactory.like("code", id));
            if (status != null)
                sp.add(ConditionFactory.equal("status", status));
            else
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "status"));
            if (doneStartTime != null)
                sp.add(ConditionFactory.greatThanEqualTo("doneAt", doneStartTime));
            if (doneEndTime != null)
                sp.add(ConditionFactory.lessThanEqualTo("doneAt", doneEndTime));
            sp.add(ConditionFactory.equal("city", city));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        Page<PurchaseOrder> res = purchaseOrderRepository.findAll(sp, pageable);
        List<PurchaseOrderListVO> resultList = res.getContent().parallelStream()
                                            .map(p -> PurchaseOrderListVO.buildVO(p, supplierService.getSupplierName(p.getSupplierId()), userService.getUser(p.getCreateBy()).getName()))
                                            .filter(Objects::nonNull).collect(Collectors.toList());
        return new PageImpl<>(resultList, pageable, res.getTotalElements());
    }

    @Override
    public PurchaseOrderDetailVO findPurchaseById(int id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(id);
        QueryContainer<PurchaseOrderProduct> sp = new QueryContainer<>();
        QueryContainer<PaymentRecord> paySp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("purchaseOrderId", id));
            paySp.add(ConditionFactory.equal("purchaseOrderId", id));
        } catch (Exception e) {
            log.error("Value is null.", e);
        }
        List<PurchaseOrderProduct> products = purchaseOrderProductRepository.findAll(sp);
        List<PurchaseProductVO> productVOS = products.parallelStream()
                                                    .map(p -> PurchaseProductVO.buildVO(p, productService.findProductNameById(p.getProductId())))
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList());
        List<PaymentRecord> paymentRecords = paymentRecordRepository.findAll(paySp);
        return PurchaseOrderDetailVO.builder().id(purchaseOrder.getId())
                                            .code(purchaseOrder.getCode())
                                            .description(purchaseOrder.getDescription())
                                            .supplierId(purchaseOrder.getSupplierId())
                                            .supplierName(supplierService.getSupplierName(purchaseOrder.getSupplierId()))
                                            .cash(purchaseOrder.getCash())
                                            .salesman(purchaseOrder.getSalesman())
                                            .status(purchaseOrder.getStatus())
                                            .doneAt(purchaseOrder.getDoneAt())
                                            .createdAt(purchaseOrder.getCreateAt())
                                            .createdById(purchaseOrder.getCreateBy())
                                            .createdByName(userService.getUser(purchaseOrder.getCreateBy()).getName())
                                            .products(productVOS)
                                            .paymentRecords(paymentRecords).build();
    }

    @Override
    public int addNewPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
        String city = CityEnum.of(CommonUtils.getCity()).getMessage();
        PurchaseOrder purchaseOrder = PurchaseOrder.builder().description(purchaseOrderDTO.getDescription())
                                                            .supplierId(purchaseOrderDTO.getSupplierId())
                                                            .cash(purchaseOrderDTO.getCash())
                                                            .salesman(purchaseOrderDTO.getSalesman())
                                                            .city(city)
                                                            .doneAt(purchaseOrderDTO.getDoneAt())
                                                            .code(documentsIdFactory.generateNextCode(DocumentsType.PURCHASE_ORDER, CityEnum.of(CommonUtils.getCity())))
                                                            .status(PurchaseOrderStatus.UNPAID.getCode())
                                                            .createAt(DateUtils.getToday())
                                                            .createBy(CommonUtils.getUserId())
                                                            .updateAt(DateUtils.getToday())
                                                            .updateBy(CommonUtils.getUserId())
                                                            .build();
        PurchaseOrder current = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        List<PurchaseOrderProduct> productList = purchaseOrderDTO.getProducts().parallelStream()
                                                                    .map(p -> {
                                                                        PurchaseOrderProduct product
                                                                                = PurchaseOrderProduct.builder()
                                                                                .purchaseOrderId(current.getId())
                                                                                .productId(p.getProductId())
                                                                                .price(p.getPrice())
                                                                                .cach(p.getCash())
                                                                                .build();
                                                                        if (p.getPriceType() == 1) { // 元/千克
                                                                            product.setWeight(p.getWeight());
                                                                        }else {  // 元/件
                                                                            product.setQuantity(p.getQuantity());
                                                                        }
                                                                        return product;
                                                                    })
                                                                    .collect(Collectors.toList());
        purchaseOrderProductRepository.saveAll(productList);
        purchaseOrderProductRepository.flush();
        return current.getId();
    }

    @Override
    public double queryUnPaidCash(String doneAtStartTime, String doneAtEndTime) {
        QueryContainer<PurchaseOrder> purchaseSp = new QueryContainer<>();
        QueryContainer<PaymentRecord> paymentSp = new QueryContainer<>();
        double upPaidCash = 0;
        double totalCash = 0;
        double paidCash = 0;
        try {
            if (doneAtStartTime != null)
                purchaseSp.add(ConditionFactory.greatThanEqualTo("doneAt", doneAtStartTime));
            if (doneAtEndTime != null)
                purchaseSp.add(ConditionFactory.lessThanEqualTo("doneAt", doneAtEndTime));
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll(purchaseSp);
            totalCash = purchaseOrders.parallelStream()
                                    .mapToDouble(PurchaseOrder::getCash)
                                    .sum();
            List<Integer> purcaseIds = purchaseOrders.parallelStream()
                                                    .map(PurchaseOrder::getId)
                                                    .collect(Collectors.toList());
            paymentSp.add(ConditionFactory.In("purchaseOrderId", purcaseIds));
            paymentSp.add(ConditionFactory.notEqual("status", PaymentRecordStatus.ABANDONED.getCode()));
            List<PaymentRecord> paymentRecords = paymentRecordRepository.findAll(paymentSp);
            paidCash = paymentRecords.parallelStream()
                                    .mapToDouble(PaymentRecord::getCash)
                                    .sum();
            upPaidCash = totalCash - paidCash;
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        return upPaidCash;
    }

    @Override
    public int addNewPaymentRecord(AddPaymentRecordDTO addPaymentRecordDTO) throws Exception{
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(addPaymentRecordDTO.getPurchaseOrderId());
        if (!PurchaseOrderStatus.of(purchaseOrder.getStatus()).paymentable())
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "该单据不能继续支付");

        double payed = getPaid(purchaseOrder.getId());
        if (purchaseOrder.getCash() - payed < addPaymentRecordDTO.getCash())
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "已付超过应收");

        PaymentRecord newPayment = PaymentRecord.builder().purchaseOrderId(addPaymentRecordDTO.getPurchaseOrderId())
                                                        .cash(addPaymentRecordDTO.getCash())
                                                        .status(PaymentRecordStatus.CONFIRMED.getCode())
                                                        .description(addPaymentRecordDTO.getDescription())
                                                        .salesman(addPaymentRecordDTO.getSalesman())
                                                        .doneAt(addPaymentRecordDTO.getDoneAt())
                                                        .createAt(DateUtils.getToday())
                                                        .createBy(CommonUtils.getUserId())
                                                        .build();
        int result = paymentRecordRepository.saveAndFlush(newPayment).getId();

        if (payed + addPaymentRecordDTO.getCash() == purchaseOrder.getCash())
            purchaseOrder.setStatus(PurchaseOrderStatus.FINISHED.getCode());
        else
            purchaseOrder.setStatus(PurchaseOrderStatus.UNFINISHED.getCode());
        purchaseOrderRepository.saveAndFlush(purchaseOrder);
        return result;
    }

    @Override
    public int deletePaymentReport(int id) throws Exception{
        PaymentRecord paymentRecord = paymentRecordRepository.getOne(id);
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(paymentRecord.getPurchaseOrderId());

        if (PaymentRecordStatus.of(paymentRecord.getStatus()) == PaymentRecordStatus.ABANDONED)
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "该单据已被废弃");

        paymentRecord.setStatus(PaymentRecordStatus.ABANDONED.getCode());
        paymentRecord.setDeleteAt(DateUtils.getToday());
        paymentRecord.setDeleteBy(CommonUtils.getUserId());
        int result = paymentRecordRepository.saveAndFlush(paymentRecord).getId();

        double paid = getPaid(purchaseOrder.getId());
        if (paid == 0)
            purchaseOrder.setStatus(PurchaseOrderStatus.UNPAID.getCode());
        else
            purchaseOrder.setStatus(PurchaseOrderStatus.UNFINISHED.getCode());
        purchaseOrderRepository.saveAndFlush(purchaseOrder);
        return result;
    }

    @Override
    public int abandonPurchaseOrder(int id) throws Exception{
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(id);
        if (!PurchaseOrderStatus.of(purchaseOrder.getStatus()).abandonable())
            throw new NJUException(ExceptionEnum.SERVER_ERROR, "该单据目前不能被废弃");

        purchaseOrder.setStatus(PurchaseOrderStatus.ABANDONED.getCode());
        return purchaseOrderRepository.saveAndFlush(purchaseOrder).getId();
    }

    private double getPaid(int purchaseOrderId) throws Exception{
        QueryContainer<PaymentRecord> sp = new QueryContainer<>();
        sp.add(ConditionFactory.equal("purchaseOrderId", purchaseOrderId));
        sp.add(ConditionFactory.equal("status", PaymentRecordStatus.CONFIRMED.getCode()));
        List<PaymentRecord> paymentRecords = paymentRecordRepository.findAll(sp);
        double payed = paymentRecords.parallelStream().mapToDouble(PaymentRecord::getCash).sum();
        return payed;
    }
}
