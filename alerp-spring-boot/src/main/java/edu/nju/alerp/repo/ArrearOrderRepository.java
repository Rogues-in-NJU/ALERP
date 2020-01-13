package edu.nju.alerp.repo;

import java.util.List;
import java.util.Map;

import edu.nju.alerp.entity.ArrearOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * 收款单dao层
 *
 * @author luhailong
 * @date 2019/12/28
 */
public interface ArrearOrderRepository extends JpaRepository<ArrearOrder, Integer>,
    JpaSpecificationExecutor<ArrearOrder> {

    /**
     * 按照人、月返回逾期金额
     *
     * @return
     */
    @Query(value =
        "select customer_id as customerId, DATE_FORMAT(due_date, '%Y-%m') as month, sum(receivable_cash - received_cash) as cash from "
            + "arrear_order where due_date<NOW() group by customer_id, month", nativeQuery = true)
    List<Map<String, Object>> getOverdueCash();
}