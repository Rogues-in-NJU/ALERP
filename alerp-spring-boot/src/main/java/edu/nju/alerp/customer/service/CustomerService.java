package edu.nju.alerp.customer.service;

import edu.nju.alerp.customer.dto.CustomerDTO;
import edu.nju.alerp.customer.dto.CustomerInfo;
import edu.nju.alerp.entity.Customer;

import java.util.List;

/**
 * @Description: 客户服务层接口
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:43
 */
public interface CustomerService {

    boolean addCustomer(CustomerDTO customerDTO);

    boolean updateCustomer(CustomerDTO customerDTO);

    Customer getCustomer(int id);

    CustomerInfo getCustomerInfo(int id);

    boolean deleteCustomer(int id);

    List<Customer> getCustomerList();
}
