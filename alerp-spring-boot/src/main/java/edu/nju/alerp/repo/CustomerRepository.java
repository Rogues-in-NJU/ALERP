package edu.nju.alerp.repo;

import edu.nju.alerp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description: 客户dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 21:38
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {
    @Query("select id from Customer c where c.name like %:name% or c.shorthand like %:name%")
    List<Integer> findCustomerIdByNameAndShorthand(@Param("name") String name);
}
