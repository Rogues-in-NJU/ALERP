package edu.nju.alerp.repo;

import edu.nju.alerp.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * @Description: 采购单dao层
 * @Author: yangguan
 * @CreateDate: 2019-12-23 20:33
 */
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer>, JpaSpecificationExecutor<PurchaseOrder> {
}
