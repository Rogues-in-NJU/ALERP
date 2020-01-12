package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.CustomerDTO;
import edu.nju.alerp.dto.SpecialPricesDTO;
import edu.nju.alerp.enums.CustomerType;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.service.CustomerService;
import edu.nju.alerp.repo.CustomerRepository;
import edu.nju.alerp.repo.SpecialPricesRepository;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.SpecialPrice;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.misc.Unsafe;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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

    @Override
    public int saveCustomer(CustomerDTO customerDTO) throws Exception {
        if (customerDTO.getId() == null) {
            Customer customer = Customer.builder()
                    .createdAt(DateUtils.getToday())
                    .createdBy(CommonUtils.getUserId())
                    .updatedAt(DateUtils.getToday())
                    .updatedBy(CommonUtils.getUserId())
                    .city(CommonUtils.getCity())
                    .build();
            BeanUtils.copyProperties(customerDTO, customer);
            List<SpecialPricesDTO> specialPricesList = customerDTO.getSpecialPrices();
            if (!CollectionUtils.isEmpty(specialPricesList)) {
                for (SpecialPricesDTO specialPricesDTO : specialPricesList) {
                    SpecialPrice specialPrice = SpecialPrice.builder()
                            .customerId(customerDTO.getId())
                            .createdAt(DateUtils.getToday())
                            .createdBy(CommonUtils.getUserId())
                            .updatedAt(DateUtils.getToday())
                            .updatedBy(CommonUtils.getUserId())
                            .productId(specialPricesDTO.getProductId())
                            .price(specialPricesDTO.getPrice())
                            .priceType(specialPricesDTO.getPriceType())
                            .build();
                    specialPricesRepository.save(specialPrice);
                }
            }
            return customerRepository.saveAndFlush(customer).getId();
        } else {
            Customer nowCustomer = getCustomer(customerDTO.getId());
            if (!nowCustomer.getUpdatedAt().equals(customerDTO.getUpdatedAt())) {
                throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "客户信息变更，请重新更新！");
            }
            nowCustomer.setUpdatedBy(CommonUtils.getUserId());
            nowCustomer.setUpdatedAt(DateUtils.getToday());
            BeanUtils.copyProperties(customerDTO, nowCustomer);
            List<SpecialPricesDTO> specialPricesList = customerDTO.getSpecialPrices();
            //取出customerid现有的特价商品
            List<Integer> origin = getSpecialPricesListByCustomerId(customerDTO.getId()).stream().map(SpecialPrice::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(specialPricesList)) {
                for (SpecialPricesDTO specialPricesDTO : specialPricesList) {
                    SpecialPrice specialPrice;
                    if (specialPricesDTO.getId() == null) {
                        specialPrice = SpecialPrice.builder()
                                .createdAt(DateUtils.getToday())
                                .createdBy(CommonUtils.getUserId())
                                .updatedAt(DateUtils.getToday())
                                .updatedBy(CommonUtils.getUserId())
                                .productId(specialPricesDTO.getProductId())
                                .price(specialPricesDTO.getPrice())
                                .priceType(specialPricesDTO.getPriceType())
                                .customerId(customerDTO.getId())
                                .build();
                        specialPricesRepository.saveAndFlush(specialPrice);
                    } else {
                        specialPrice = specialPricesRepository.getOne(specialPricesDTO.getId());
                        specialPrice.setUpdatedAt(DateUtils.getToday());
                        specialPrice.setUpdatedBy(CommonUtils.getUserId());
                        specialPricesRepository.save(specialPrice);
                    }
                    List<SpecialPricesDTO> nowDTOList = specialPricesList.stream().filter(n -> n.getId() != null).collect(Collectors.toList());
                    List<Integer> nowIdList = nowDTOList.stream().map(SpecialPricesDTO::getId).collect(Collectors.toList());
                    //比较
                    origin.forEach(o -> {
                        if (!nowIdList.contains(o)) {
                            SpecialPrice s = specialPricesRepository.getOne(o);
                            s.setDeletedAt(DateUtils.getToday());
                            specialPricesRepository.save(s);
                        }
                    });
                }
            }
            return customerRepository.save(nowCustomer).getId();
        }
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
    public int deleteCustomer(int id) throws Exception {
        Customer customer = getCustomer(id);
        if (customer == null) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "客户id不存在！");
        }
        if (customer.getDeletedAt() != null) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "删除客户失败，该客户已被删除！");
        }
        customer.setDeletedAt(DateUtils.getToday());
        customer.setDeletedBy(CommonUtils.getUserId());
        //考虑到客户为懒删除，建议暂存特惠价格数据
        return customerRepository.save(customer).getId();
    }

    @Override
    public List<Customer> getCustomerList() {
        return customerRepository.findAll();
    }


    @Override
    public Page<Customer> getCustomerListByName(Pageable pageable, String name) {
        QueryContainer<Customer> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.isNull("deletedAt"));
            sp.add(ConditionFactory.equal("city", CommonUtils.getCity()));
            if (!"".equals(name)) {
                List<Condition> fuzzyMatch = new ArrayList<>();
                fuzzyMatch.add(ConditionFactory.like("name", name));
                fuzzyMatch.add(ConditionFactory.like("shorthand", name));
                sp.add(ConditionFactory.or(fuzzyMatch));
            }
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        return sp.isEmpty() ? customerRepository.findAll(pageable) : customerRepository.findAll(sp, pageable);
    }

    @Override
    public List<Integer> getCustomerListInCash() {
        return customerRepository.findCustomerListInCash();
    }

    @Override
    public List<Integer> getCustomerListInMonth() {
        return customerRepository.findCustomerListInMonth();
    }

}
