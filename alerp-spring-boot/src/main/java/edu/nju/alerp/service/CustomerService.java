package edu.nju.alerp.service;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.SpecialPrice;
import edu.nju.alerp.dto.CustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description: 客户服务层接口
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:43
 */
public interface CustomerService {

    int saveCustomer(CustomerDTO customerDTO);

    Customer getCustomer(int id);

    List<SpecialPrice> getSpecialPricesListByCustomerId(int id);

    boolean deleteCustomer(int id);

    List<Customer> getCustomerList();

    Page<Customer> getCustomerListByName(Pageable pageable, String name);
}
