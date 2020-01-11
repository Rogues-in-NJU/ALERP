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

    @Query("select s from ShippingOrder s where s.customerId in :customerIdList")
    List<ShippingOrder> findByCustomerList(@Param("customerIdList") List<Integer> customerIdList);
}
