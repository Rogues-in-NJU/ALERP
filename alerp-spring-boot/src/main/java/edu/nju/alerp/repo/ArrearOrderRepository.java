package edu.nju.alerp.repo;

import edu.nju.alerp.entity.ArrearOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 收款单dao层
 *
 * @author luhailong
 * @date 2019/12/28
 */
public interface ArrearOrderRepository extends JpaRepository<ArrearOrder, Integer>,
    JpaSpecificationExecutor<ArrearOrder> {
}