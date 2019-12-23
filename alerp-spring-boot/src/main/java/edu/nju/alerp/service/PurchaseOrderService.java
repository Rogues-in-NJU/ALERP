package edu.nju.alerp.service;


import edu.nju.alerp.entity.PurchaseOrder;
import edu.nju.alerp.vo.PurchaseOrderDetailVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description: 采购单服务接口
 * @Author: yangguan
 * @CreateDate: 2019-12-23 21:28
 */
public interface PurchaseOrderService {

    public List<PurchaseOrder> findAll();

    public Page<PurchaseOrder> findAllByPage(Pageable pageable, String id,
                                             Integer status, String doneStartTime,
                                             String doneEndTime);

    public PurchaseOrderDetailVO findPurchaseById(int id);
}
