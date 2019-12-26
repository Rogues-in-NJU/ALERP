package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.AddPaymentRecordDTO;
import edu.nju.alerp.dto.PurchaseOrderDTO;
import edu.nju.alerp.entity.PaymentRecord;
import edu.nju.alerp.entity.PurchaseOrder;
import edu.nju.alerp.entity.PurchaseOrderProduct;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.enums.PaymentRecordStatus;
import edu.nju.alerp.enums.PurchaseOrderStatus;
import edu.nju.alerp.repo.PaymentRecordRepository;
import edu.nju.alerp.repo.PurchaseOrderProductRepository;
import edu.nju.alerp.repo.PurchaseOrderRepository;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.service.PurchaseOrderService;
import edu.nju.alerp.service.SupplierService;
import edu.nju.alerp.util.TimeUtil;
import edu.nju.alerp.vo.PurchaseOrderDetailVO;
import edu.nju.alerp.vo.PurchaseOrderListVO;
import edu.nju.alerp.vo.PurchaseProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Override
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Page<PurchaseOrderListVO> findAllByPage(Pageable pageable, String id, Integer status, String doneStartTime, String doneEndTime) {
        QueryContainer<PurchaseOrder> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.like("code", id));
            sp.add(ConditionFactory.equal("status", status));
            sp.add(ConditionFactory.between("done_at", doneStartTime, doneEndTime));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        List<PurchaseOrder> res = purchaseOrderRepository.findAll(sp);
        List<PurchaseOrderListVO> resultList = res.parallelStream()
                                            .map(p -> PurchaseOrderListVO.buildVO(p, supplierService.getSupplierName(p.getSupplierId()), "创建者的名字"))
                                            .filter(Objects::nonNull).collect(Collectors.toList());
        return new PageImpl<>(resultList, pageable, resultList.size());
    }

    @Override
    public PurchaseOrderDetailVO findPurchaseById(int id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(id);
        QueryContainer<PurchaseOrderProduct> sp = new QueryContainer<>();
        QueryContainer<PaymentRecord> paySp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("product_id", id));
            paySp.add(ConditionFactory.equal("purchase_order_id", id));
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
                                            // todo .createdByName()
                                            .products(productVOS)
                                            .paymentRecords(paymentRecords).build();
    }

    @Override
    public int addNewPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder purchaseOrder = PurchaseOrder.builder().description(purchaseOrderDTO.getDescription())
                                                            .supplierId(purchaseOrderDTO.getSupplierId())
                                                            .cash(purchaseOrderDTO.getCash())
                                                            .salesman(purchaseOrderDTO.getSalesman())
                                                            .doneAt(purchaseOrderDTO.getDoneAt())
                                                            .code(documentsIdFactory.generateNextCode(DocumentsType.PURCHASE_ORDER))
                                                            .status(PurchaseOrderStatus.UNFINISHED.getCode())
                                                            .createAt(TimeUtil.dateFormat(new Date()))
                                                            //createBy
                                                            .updateAt(TimeUtil.dateFormat(new Date()))
                                                            // update_by
                                                            .build();
        PurchaseOrder current = purchaseOrderRepository.saveAndFlush(purchaseOrder);

        List<PurchaseOrderProduct> productList = purchaseOrderDTO.getProducts().parallelStream()
                                                                    .map(p -> PurchaseOrderProduct.builder()
                                                                                .purchaseOrderId(current.getId())
                                                                                .productId(p.getProductId())
                                                                                .quantity(p.getQuantity())
                                                                                .weight(p.getWeight())
                                                                                .price(p.getPrice())
                                                                                .cach(p.getCash())
                                                                                .build())
                                                                    .collect(Collectors.toList());
        purchaseOrderProductRepository.saveAll(productList);
        purchaseOrderProductRepository.flush();
        return current.getId();
    }

    @Override
    public int addNewPaymentRecord(AddPaymentRecordDTO addPaymentRecordDTO) throws Exception{
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(addPaymentRecordDTO.getPurchaseOrderId());
        if (!PurchaseOrderStatus.of(purchaseOrder.getStatus()).paymentable())
            throw new Exception(); // todo 不能支付错误

        QueryContainer<PaymentRecord> sp = new QueryContainer<>();
        sp.add(ConditionFactory.equal("purchase_order_id", purchaseOrder.getId()));
        sp.add(ConditionFactory.equal("status", PaymentRecordStatus.CONFIRMED));
        List<PaymentRecord> paymentRecords = paymentRecordRepository.findAll(sp);
        double payed = paymentRecords.parallelStream().mapToDouble(PaymentRecord::getCash).sum();
        if (purchaseOrder.getCash() - payed < addPaymentRecordDTO.getCash())
            throw new Exception(); //todo 付了过多的钱的异常

        PaymentRecord newPayment = PaymentRecord.builder().purchaseOrderId(addPaymentRecordDTO.getPurchaseOrderId())
                                                        .cash(addPaymentRecordDTO.getCash())
                                                        .status(PaymentRecordStatus.CONFIRMED.getCode())
                                                        .description(addPaymentRecordDTO.getDescription())
                                                        .salesman(addPaymentRecordDTO.getSalesman())
                                                        .doneAt(addPaymentRecordDTO.getDoneAt())
                                                        .createAt(TimeUtil.dateFormat(new Date()))
                                                        // todo .createBy()
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
            throw new Exception(); // todo 已废弃的不能再废弃

        paymentRecord.setStatus(PaymentRecordStatus.ABANDONED.getCode());
        int result = paymentRecordRepository.saveAndFlush(paymentRecord).getId();

        purchaseOrder.setStatus(PurchaseOrderStatus.UNFINISHED.getCode());
        purchaseOrderRepository.saveAndFlush(purchaseOrder);
        return result;
    }

    @Override
    public int abandonPurchaseOrder(int id) throws Exception{
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(id);
        if (!PurchaseOrderStatus.of(purchaseOrder.getStatus()).abandonable())
            throw new Exception(); // todo 不能废弃

        purchaseOrder.setStatus(PurchaseOrderStatus.ABANDONED.getCode());
        return purchaseOrderRepository.saveAndFlush(purchaseOrder).getId();
    }
}
