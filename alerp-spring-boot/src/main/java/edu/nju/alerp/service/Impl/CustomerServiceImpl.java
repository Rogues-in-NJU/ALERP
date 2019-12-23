package edu.nju.alerp.service.Impl;

import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.CustomerDTO;
import edu.nju.alerp.dto.SpecialPricesDTO;
import edu.nju.alerp.service.CustomerService;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.repo.CustomerRepository;
import edu.nju.alerp.repo.SpecialPricesRepository;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.SpecialPrice;
import edu.nju.alerp.util.ListResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 客户服务层接口实现
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 20:44
 */
@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SpecialPricesRepository specialPricesRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public int saveCustomer(CustomerDTO customerDTO) {
        Customer customer = null;
        if (customerDTO.getId() == null) {
            customer = Customer.builder()
                    .createdAt(sdf.format(new Date()))
                    .build();
            BeanUtils.copyProperties(customerDTO, customer);
            //前台需要传操作人信息，记录创建者是谁
            List<SpecialPricesDTO> specialPricesList = customerDTO.getSpecialPricesList();
            for (SpecialPricesDTO specialPricesDTO : specialPricesList) {
                SpecialPrice specialPrice = SpecialPrice.builder()
                        .createdAt(sdf.format(new Date()))
//                    .createdById() //待获取用户id
                        .build();
                BeanUtils.copyProperties(specialPricesDTO, specialPrice);
                specialPricesRepository.save(specialPrice);
            }
        } else {
            customer = Customer.builder()
                    .updatedAt(sdf.format(new Date()))
                    .build();
            BeanUtils.copyProperties(customerDTO, customer);
            List<SpecialPricesDTO> specialPricesList = customerDTO.getSpecialPricesList();
            for (SpecialPricesDTO specialPricesDTO : specialPricesList) {
                SpecialPrice specialPrice = specialPricesRepository.getOne(specialPricesDTO.getId());
                if (specialPrice == null) {
                    specialPrice = SpecialPrice.builder()
                            .createdAt(sdf.format(new Date()))
//                    .createdById() //待获取用户id
                            .build();
                    BeanUtils.copyProperties(specialPricesDTO, specialPrice);
                } else {
                    specialPrice.setUpdatedAt(sdf.format(new Date()));
//                specialPrices.setUpdateById();//待获取用户id
                }
                specialPricesRepository.save(specialPrice);
            }
        }

        return customerRepository.saveAndFlush(customer).getId();
    }

    @Override
    public Customer getCustomer(int id) {
        return customerRepository.getOne(id);
    }

    @Override
    public List<SpecialPrice> getSpecialPricesListByCustomerId(int id) {
        return specialPricesRepository.findByCustomerId(id);
    }

    @Override
    public boolean deleteCustomer(int id) {
        Customer customer = getCustomer(id);
        if (customer == null) {
            return false;
        }
        customer.setDeletedAt(sdf.format(new Date()));
        customerRepository.save(customer);
        //考虑到客户为懒删除，建议暂存特惠价格数据
        return false;
    }

    @Override
    public List<Customer> getCustomerList() {
        return customerRepository.findAll();
    }

    @Override
    public Page<Customer> getCustomerListByName(Pageable pageable, String name) {
        QueryContainer<Customer> sp = new QueryContainer<>();
        try {
            List<Condition> fuzzyMatch = new ArrayList<>();
            fuzzyMatch.add(ConditionFactory.like("name", name));
            fuzzyMatch.add(ConditionFactory.like("shorthand", name));
            sp.add(ConditionFactory.or(fuzzyMatch));
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        return customerRepository.findAll(sp, pageable);
    }

}
