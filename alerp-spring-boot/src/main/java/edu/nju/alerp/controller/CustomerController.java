package edu.nju.alerp.controller;

import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.entity.ProcessingOrder;
import edu.nju.alerp.enums.CustomerType;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.service.ProcessOrderService;
import edu.nju.alerp.util.DateUtils;
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
    ProcessOrderService processOrderService;


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
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, name = "查看客户详细信息")
    public ResponseResult<CustomerVO> CustomerVO(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        Customer customer = customerService.getCustomer(id);
        if (customer == null) {
            return ResponseResult.fail(ExceptionEnum.ILLEGAL_PARAM, "客户不存在");
        }
        boolean inactive;
        ProcessingOrder processingOrder = processOrderService.getLatestOrder(id);
        if (processingOrder == null) {
            inactive = true;
        } else {
            long diff = DateUtils.getTimeDifference(processingOrder.getCreateAt());
            inactive = diff >= 25;
        }
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
                .specialPrices(specialPricesVOList)
                .inactive(inactive)
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
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET, name = "删除客户")
    public ResponseResult<Integer> delete(
            @NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(customerService.deleteCustomer(id));
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    /**
     * 获取客户列表
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, name = "获取客户列表")
    public ResponseResult<ListResponse> list(@RequestParam(value = "pageIndex") int pageIndex,
                                             @RequestParam(value = "pageSize") int pageSize,
                                             @RequestParam(value = "name", required = false) String name) {
        Page<Customer> page = customerService.getCustomerListByName(PageRequest.of(pageIndex - 1, pageSize), name, null);
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }

    /**
     * 获取月结客户列表
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/monthList", method = RequestMethod.GET, name = "获取客户列表")
    public ResponseResult<ListResponse> monthList(@RequestParam(value = "pageIndex") int pageIndex,
                                                  @RequestParam(value = "pageSize") int pageSize) {
        Page<Customer> page = customerService.getCustomerListByName(PageRequest.of(pageIndex - 1, pageSize), null, CustomerType.MONTH.getCode());
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }

    /**
     * 新增客户/修改客户信息
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST, name = "新增客户/修改客户信息")
    public ResponseResult<Integer> saveCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        try {
            return ResponseResult.ok(customerService.saveCustomer(customerDTO));
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }
}
