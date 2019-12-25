package edu.nju.alerp.service.impl;


import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.entity.PurchaseOrder;
import edu.nju.alerp.repo.PurchaseOrderRepository;
import edu.nju.alerp.service.PurchaseOrderService;
import edu.nju.alerp.vo.PurchaseOrderDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public Page<PurchaseOrder> findAllByPage(Pageable pageable, String id, Integer status, String doneStartTime, String doneEndTime) {
        QueryContainer<PurchaseOrder> sp = new QueryContainer<>();
        try {
            if (id != null)
                sp.add(ConditionFactory.like("code", id));
            if (status != null)
                sp.add(ConditionFactory.equal("status", status));
            if (doneStartTime != null && doneEndTime != null)
                sp.add(ConditionFactory.between("done_at", doneStartTime, doneEndTime));
        }catch (Exception e) {
            log.error("Value is null.", e);
        }
        return purchaseOrderRepository.findAll(sp, pageable);
    }

    @Override
    public PurchaseOrderDetailVO findPurchaseById(int id) {
        return null;
    }
}
