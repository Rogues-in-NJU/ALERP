package edu.nju.alerp.Controller;

import edu.nju.alerp.Dto.CustomerDTO;
import edu.nju.alerp.Dto.CustomerInfo;
import edu.nju.alerp.Dto.SpecialPrciesInfo;
import edu.nju.alerp.Service.ProductService;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.Service.CustomerService;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.SpecialPrices;
import edu.nju.alerp.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    /**
     * 查看客户详细信息
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseResult<CustomerInfo> customerInfo(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        Customer customer = customerService.getCustomer(id);
        List<SpecialPrices> specialPricesList = customerService.getSpecialPricesListByCustomerId(id);
        List<SpecialPrciesInfo> specialPrciesInfoList = new ArrayList<>();
        for (SpecialPrices specialPrices : specialPricesList) {
            SpecialPrciesInfo specialPrciesInfo = SpecialPrciesInfo.builder()
                    .createdByName(userService.getUser(specialPrices.getCreatedById()).getName())
                    .productName(productService.findProductById(specialPrices.getProductId()).getName())
                    .build();
            BeanUtils.copyProperties(specialPrices, specialPrciesInfo);
            specialPrciesInfoList.add(specialPrciesInfo);
        }
        CustomerInfo customerInfo = CustomerInfo.builder()
                .specialPrciesInfoList(specialPrciesInfoList)
                .build();
        BeanUtils.copyProperties(customer, customerInfo);

        return ResponseResult.ok(customerInfo);
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
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseResult<ListResponse> list(@RequestParam(value = "pageIndex") int pageIndex,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value = "name") String name) {
        return ResponseResult.ok(customerService.getCustomerListByName(pageIndex, pageSize, name));
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
