package edu.nju.alerp.repo;

import edu.nju.alerp.entity.ShippingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 出货单dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 21:37
 */
public interface ShippingOrderRepository extends JpaRepository<ShippingOrder, Integer> {
}
