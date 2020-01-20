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
     * 按照人、月返回欠款金额
     *
     * @return
     */
    @Query(value =
        "select customer_id as customerId, DATE_FORMAT(due_date, '%Y-%m') as month, sum(receivable_cash - "
            + "received_cash) as cash from "
            + "arrear_order where status = 1 or  status = 2 group by customer_id, month", nativeQuery = true)
    List<Map<String, Object>> getOverdueCashBySbByMonth();

    /**
     * 按照月返回欠款金额。
     * 这里的数据结构太过复杂，故意多查一次数据库牺牲性能简化逻辑层的数据结构拼装。
     * 其实只需要上面这个按人按月的统计方法就可以得到所有数据的。
     * @return
     */
    @Query(value = "select DATE_FORMAT(due_date, '%Y-%m') as month, sum(receivable_cash - received_cash) as cash from"
        + " arrear_order where status = 1 or  status = 2 group by  month", nativeQuery = true)
    List<Map<String, Object>> getOverdueCashByMonth();
}