package edu.nju.alerp.repo;

import edu.nju.alerp.entity.ProcessingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * @Description: 加工单Repo
 * @Author: yangguan
 * @CreateDate: 2019-12-24 15:55
 */
public interface ProcessingOrderRepository extends JpaRepository<ProcessingOrder, Integer>, JpaSpecificationExecutor<ProcessingOrder> {
}
