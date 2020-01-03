package edu.nju.alerp.controller;

import edu.nju.alerp.vo.CustomerVO;
import edu.nju.alerp.dto.CustomerDTO;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.service.CustomerService;
import edu.nju.alerp.entity.Customer;
import edu.nju.alerp.entity.SpecialPrice;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.vo.SpecialPricesVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseResult<CustomerVO> CustomerVO(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        Customer customer = customerService.getCustomer(id);
        List<SpecialPrice> specialPriceList = customerService.getSpecialPricesListByCustomerId(id);
        List<SpecialPricesVO> specialPricesVOList = new ArrayList<>();
        for (SpecialPrice specialPrice : specialPriceList) {
            SpecialPricesVO specialPricesVO = SpecialPricesVO.builder()
                    .createdByName(userService.getUser(specialPrice.getCreatedBy()).getName())
                    .productName(productService.findProductById(specialPrice.getProductId()).getName())
                    .build();
            BeanUtils.copyProperties(specialPrice, specialPricesVO);
            specialPricesVOList.add(specialPricesVO);
        }
        CustomerVO customerVO = CustomerVO.builder()
                .specialPricesVOList(specialPricesVOList)
                .build();
        BeanUtils.copyProperties(customer, customerVO);

        return ResponseResult.ok(customerVO);
    }

    /**
     * 删除客户
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResponseResult<Integer> delete(
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
    public ResponseResult<ListResponse> list(@RequestParam(value = "pageIndex") int pageIndex,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value = "name", required = false, defaultValue = "") String name) {
        Page<Customer> page = customerService.getCustomerListByName(PageRequest.of(pageIndex - 1, pageSize), name);
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }

    /**
     * 新增客户/修改客户信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseResult<List<Customer>> saveCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        customerService.saveCustomer(customerDTO);
        List<Customer> customerList = customerService.getCustomerList();
        return ResponseResult.ok(customerList);
    }
}
