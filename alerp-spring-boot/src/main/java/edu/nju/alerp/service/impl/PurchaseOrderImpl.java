package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.PurchaseOrderDTO;
import edu.nju.alerp.entity.PurchaseOrder;
import edu.nju.alerp.entity.PurchaseOrderProduct;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.enums.PurchaseOrderStatus;
import edu.nju.alerp.repo.PurchaseOrderProductRepository;
import edu.nju.alerp.repo.PurchaseOrderRepository;
import edu.nju.alerp.service.PurchaseOrderService;
import edu.nju.alerp.util.TimeUtil;
import edu.nju.alerp.vo.PurchaseOrderDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
    private DocumentsIdFactory documentsIdFactory;

    @Override
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Page<PurchaseOrder> findAllByPage(Pageable pageable, String id, Integer status, String doneStartTime, String doneEndTime) {
        QueryContainer<PurchaseOrder> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.like("code", id));
            sp.add(ConditionFactory.equal("status", status));
            sp.add(ConditionFactory.between("done_at", doneStartTime, doneEndTime));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        return purchaseOrderRepository.findAll(sp, pageable);
    }

    @Override
    public PurchaseOrderDetailVO findPurchaseById(int id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(id);
        QueryContainer<PurchaseOrderProduct> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("product_id", id));
        } catch (Exception e) {
            log.error("Value is null.", e);
            List<PurchaseOrderProduct> products = purchaseOrderProductRepository.findAll(sp);
        }
        return null;
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
}
