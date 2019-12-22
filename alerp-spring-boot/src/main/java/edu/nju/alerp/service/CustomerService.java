package edu.nju.alerp.service;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.SpecialPrice;
import edu.nju.alerp.dto.CustomerDTO;
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

    List<SpecialPrice> getSpecialPricesListByCustomerId(int id);

    boolean deleteCustomer(int id);

    List<Customer> getCustomerList();

    ListResponse getCustomerListByName(int pageIndex, int pageSize, String name);
}
