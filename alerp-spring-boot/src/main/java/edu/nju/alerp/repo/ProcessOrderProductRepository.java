package edu.nju.alerp.repo;

import edu.nju.alerp.entity.ProcessOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Description: 加工单-商品关联repo
 * @Author: yangguan
 * @CreateDate: 2019-12-24 15:56
 */
public interface ProcessOrderProductRepository extends JpaRepository<ProcessOrderProduct, Integer>, JpaSpecificationExecutor<ProcessOrderProduct> {
    List<ProcessOrderProduct> findAllByProcessOrderId(int processOrderId);
}
