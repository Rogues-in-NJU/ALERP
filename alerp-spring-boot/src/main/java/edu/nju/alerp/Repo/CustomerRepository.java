package edu.nju.alerp.Repo;

import edu.nju.alerp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 客户dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 21:38
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
