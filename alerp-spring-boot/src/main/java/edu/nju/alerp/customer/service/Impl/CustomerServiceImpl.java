package edu.nju.alerp.customer.service.Impl;

import edu.nju.alerp.customer.dao.CustomerRepository;
import edu.nju.alerp.customer.dao.SpecialPricesRepository;
import edu.nju.alerp.customer.dto.CustomerDTO;
import edu.nju.alerp.customer.dto.CustomerInfo;
import edu.nju.alerp.customer.dto.SpecialPricesDTO;
import edu.nju.alerp.customer.service.CustomerService;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.SpecialPrices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: 客户服务层接口实现
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:44
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SpecialPricesRepository specialPricesRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public boolean addCustomer(CustomerDTO customerDTO) {
        Customer customer = Customer.builder()
                .created_at(sdf.format(new Date()))
                .build();
        BeanUtils.copyProperties(customerDTO, customer);
        //前台需要传操作人信息，记录创建者是谁
        List<SpecialPricesDTO> specialPricesList = customerDTO.getSpecialPricesList();
        for (SpecialPricesDTO specialPricesDTO : specialPricesList) {
            SpecialPrices specialPrices = SpecialPrices.builder()
                    .createdAt(sdf.format(new Date()))
//                    .createdById() //待获取用户id
                    .build();
            BeanUtils.copyProperties(specialPricesDTO, specialPrices);
            specialPricesRepository.save(specialPrices);
        }
        customerRepository.save(customer);
        return false;
    }

    @Override
    public boolean updateCustomer(CustomerDTO customerDTO) {
        Customer customer = Customer.builder()
                .updated_at(sdf.format(new Date()))
                .build();
        BeanUtils.copyProperties(customerDTO, customer);
        List<SpecialPricesDTO> specialPricesList = customerDTO.getSpecialPricesList();
        for (SpecialPricesDTO specialPricesDTO : specialPricesList) {
            SpecialPrices specialPrices = specialPricesRepository.getOne(specialPricesDTO.getId());
            if(specialPrices == null){
                specialPrices = SpecialPrices.builder()
                        .createdAt(sdf.format(new Date()))
//                    .createdById() //待获取用户id
                        .build();
                BeanUtils.copyProperties(specialPricesDTO, specialPrices);
            }
            else{
                specialPrices.setUpdateAt(sdf.format(new Date()));
//                specialPrices.setUpdateById();//待获取用户id
            }
            specialPricesRepository.save(specialPrices);
        }
        customerRepository.save(customer);

        return false;
    }

    @Override
    public Customer getCustomer(int id) {
        return customerRepository.getOne(id);
    }

    @Override
    public CustomerInfo getCustomerInfo(int id) {
        Customer customer = getCustomer(id);
        //todo 特惠价格 待关神商品模块更新
        CustomerInfo customerInfo = CustomerInfo.builder()
                .specialPricesDTOList(null)
                .build();
        BeanUtils.copyProperties(customer, customerInfo);

        return customerInfo;
    }

    @Override
    public boolean deleteCustomer(int id) {
        Customer customer = getCustomer(id);
        if (customer == null) {
            return false;
        }
        customer.setDeleted_at(sdf.format(new Date()));
        customerRepository.save(customer);
        //考虑到客户为懒删除，建议暂存特惠价格数据
        return false;
    }

    @Override
    public List<Customer> getCustomerList() {
        return customerRepository.findAll();
    }
}
