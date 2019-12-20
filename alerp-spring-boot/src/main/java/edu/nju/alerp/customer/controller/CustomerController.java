package edu.nju.alerp.customer.controller;

import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.customer.dto.CustomerDTO;
import edu.nju.alerp.customer.service.CustomerService;
import edu.nju.alerp.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 客户Controller层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:23
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    /**
     * 查看客户详细信息
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseResult<Boolean> customerInfo(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(customerService.deleteCustomer(id));
    }

    /**
     * 删除客户
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResponseResult<Boolean> delete(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(customerService.deleteCustomer(id));
    }

    /**
     * 获取客户列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult<List<Customer>> list() {
        List<Customer> customerList = customerService.getCustomerList();
        return ResponseResult.ok(customerList);
    }

    /**
     * 新增客户/修改客户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<List<Customer>> updateUser(@Valid @RequestBody CustomerDTO customerDTO) {
        if (customerDTO.getId() == null) {
            customerService.addCustomer(customerDTO);
        } else {
            customerService.updateCustomer(customerDTO);
        }
        List<Customer> customerList = customerService.getCustomerList();
        return ResponseResult.ok(customerList);
    }
}
