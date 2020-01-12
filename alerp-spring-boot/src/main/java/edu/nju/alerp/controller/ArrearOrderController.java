package edu.nju.alerp.controller;

import java.util.List;

import com.google.common.collect.Lists;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.ArrearOrderDueDateDTO;
import edu.nju.alerp.dto.ArrearOrderInvoiceNumberDTO;
import edu.nju.alerp.entity.ArrearOrder;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.service.ArrearOrderService;
import edu.nju.alerp.service.CustomerService;
import edu.nju.alerp.service.ShippingOrderService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.vo.ArrearDetailVO;
import edu.nju.alerp.vo.ArrearOrderListContentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收款单controller
 *
 * @author luhailong
 * @date 2019/12/28
 */
@Slf4j
@RestController
@Validated
@RequestMapping(value = "/api/arrear-order")
public class ArrearOrderController {

    @Autowired
    private ArrearOrderService arrearOrderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShippingOrderService shippingOrderService;

    @Autowired
    private UserService userService;

    /**
     * 获取收款单详情
     *
     * @param id
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseResult<ArrearDetailVO> findArrearDetails(@PathVariable int id) {
        return ResponseResult.ok(arrearOrderService.findArrearDetails(id));
    }

    /**
     * 修改收款单截止日期
     *
     * @param dto
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/due-date", method = RequestMethod.POST)
    public ResponseResult<Integer> updateDueDate(@RequestBody ArrearOrderDueDateDTO dto) {
        return ResponseResult.ok(arrearOrderService.updateDueDate(dto.getId(), dto.getDueDate()));
    }

    /**
     * 修改发票流水号
     *
     * @param dto
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/invoice-number", method = RequestMethod.POST)
    public ResponseResult<Integer> updateInvoiceNumber(@RequestBody ArrearOrderInvoiceNumberDTO dto) {
        return ResponseResult.ok(arrearOrderService.updateInvoiceNumber(dto.getId(), dto.getInvoiceNumber()));
    }



    /**
     * 根据如下搜索条件来搜索收款单，返回收款单列表
     * 注：此处前端传参为id，但是实际上搜索的是code：单据编码
     *
     * @param pageIndex
     * @param pageSize
     * @param code
     * @param customerName
     * @param status
     * @param invoiceNumber
     * @param shippingOrderId
     * @param createAtStartTime
     * @param createAtEndTime
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult<ListResponse> getArrearOrderList(@RequestParam(value = "pageIndex") int pageIndex,
        @RequestParam(value = "pageSize") int pageSize,
        @RequestParam(value = "id", required = false, defaultValue = "") String code,
        @RequestParam(value = "customerName", required = false, defaultValue = "") String customerName,
        @RequestParam(value = "status", required = false) Integer status,
        @RequestParam(value = "invoiceNumber", required = false, defaultValue = "") String invoiceNumber,
        @RequestParam(value = "shippingOrderId", required = false) Integer shippingOrderId,
        @RequestParam(value = "createAtStartTime", required = false, defaultValue = "") String createAtStartTime,
        @RequestParam(value = "createAtEndTime", required = false, defaultValue = "") String createAtEndTime) {
        // 分页相关参数
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        // 查询数据库值
        Page<ArrearOrder> page = arrearOrderService.getArrearOrderList(pageable, code,
            customerName, status, invoiceNumber, shippingOrderId, createAtStartTime, createAtEndTime);

        // 构造返回VO
        List<ArrearOrderListContentVO> result = Lists.newArrayList();
        page.getContent().forEach(s -> {
            ShippingOrder shippingOrder = shippingOrderService.getShippingOrderByArrearOrderId(s.getId());
            ArrearOrderListContentVO targetVO = ArrearOrderListContentVO.builder().
                id(s.getId()).
                code(s.getCode()).
                customerId(s.getCustomerId()).
                customerName(customerService.getCustomer(s.getCustomerId()).getName()).
                shippingOrderId(shippingOrder.getId()).
                shippingOrderCode(shippingOrder.getCode()).
                receivableCash(s.getReceivableCash()).
                receivedCash(s.getReceivedCash()).
                dueDate(s.getDueDate()).
                overDue(s.getDueDate().compareTo(DateUtils.getToday()) > 0).
                status(s.getStatus()).
                createdAt(s.getCreatedAt()).
                createdById(s.getCreatedBy()).
                createdByName(userService.getUser(s.getCreatedBy()).getName()).
                build();
            result.add(targetVO);
        });

        return ResponseResult.ok(ListResponseUtils
            .generateResponse(new PageImpl<>(result, pageable, page.getTotalElements()), pageIndex, pageSize));
    }

}
