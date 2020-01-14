package edu.nju.alerp.repo;

import edu.nju.alerp.entity.ProcessingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * @Description: 加工单Repo
 * @Author: yangguan
 * @CreateDate: 2019-12-24 15:55
 */
public interface ProcessingOrderRepository extends JpaRepository<ProcessingOrder, Integer>, JpaSpecificationExecutor<ProcessingOrder> {

    @Query(nativeQuery = true, value = "select * from processing_order p where customer_id = ?1 order by create_at desc limit 1")
    ProcessingOrder findLatestOrder(int customerId);
}
