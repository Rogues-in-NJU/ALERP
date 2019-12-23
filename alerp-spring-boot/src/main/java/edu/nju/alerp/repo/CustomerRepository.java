package edu.nju.alerp.repo;

import edu.nju.alerp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 客户dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 21:38
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {
}
