package edu.nju.alerp.repo;


import edu.nju.alerp.entity.PurchaseOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 采购单-商品关联repo
 * @Author: yangguan
 * @CreateDate: 2019-12-24 14:06
 */
public interface PurchaseOrderProductRepository extends JpaRepository<PurchaseOrderProduct, Integer>, JpaSpecificationExecutor<PurchaseOrderProduct> {
}
