package edu.nju.alerp.repo;

import edu.nju.alerp.entity.ShippingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description: 出货单dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 21:37
 */
public interface ShippingOrderRepository extends JpaRepository<ShippingOrder, Integer>, JpaSpecificationExecutor<ShippingOrder> {

    @Query("select s from ShippingOrder s where s.customerId in :customerIdList and s.createdAt >= :startDate and s.createdAt <= :endDate and s.status <> 2")
    List<ShippingOrder> findByCustomerList(@Param("customerIdList") List<Integer> customerIdList, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select count(s) from ShippingOrder s where s.createdAt >= :startDate and s.createdAt <= :endDate and s.status <> 2")
    Integer findTotalNum(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select sum(s.receivableCash) from ShippingOrder s where s.createdAt >= :startDate and s.createdAt <= :endDate and s.status <> 2")
    Double findTotalInCome(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据收款单id找到一条出货单记录
     *
     * @param arrearOrderId
     * @return
     */
    ShippingOrder findShippingOrderByArrearOrderId(int arrearOrderId);
}
