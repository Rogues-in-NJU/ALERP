package edu.nju.alerp.controller;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.ArrearOrderDueDateDTO;
import edu.nju.alerp.dto.ArrearOrderInvoiceNumberDTO;
import edu.nju.alerp.dto.InvoiceNumberTogetherDTO;
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
import edu.nju.alerp.vo.OverdueCashVO;
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
@RequestMapping(value = "/api")
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
    @RequestMapping(value = "/arrear-order/{id}", method = RequestMethod.GET, name = "获取收款单详情")
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
    @RequestMapping(value = "/arrear-order/due-date", method = RequestMethod.POST, name = "修改收款单截止日期")
    public ResponseResult<Integer> updateDueDate(@RequestBody ArrearOrderDueDateDTO dto) {
        return ResponseResult.ok(arrearOrderService.updateDueDate(dto));
    }

    /**
     * 修改发票流水号
     *
     * @param dto
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/arrear-order/invoice-number", method = RequestMethod.POST, name = "修改发票流水号")
    public ResponseResult<Integer> updateInvoiceNumber(@RequestBody ArrearOrderInvoiceNumberDTO dto) {
        return ResponseResult.ok(arrearOrderService.updateInvoiceNumber(dto));
    }

    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/arrear-order/invoice-number-together", method = RequestMethod.POST, name = "批量输入发票流水号")
    public ResponseResult<Boolean> updateInvoiceNumberTogether(@RequestBody InvoiceNumberTogetherDTO dto) {
        for (Integer id : dto.getShippingOrderIds()) {
            ShippingOrder shippingOrder = shippingOrderService.getShippingOrder(id);
            ArrearOrder arrearOrder = arrearOrderService.getOne(shippingOrder.getArrearOrderId());
            arrearOrder.setInvoiceNumber(dto.getInvoiceNumber());
            arrearOrderService.saveArrearOrder(arrearOrder);
        }
        return ResponseResult.ok(true);
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
    @RequestMapping(value = "/arrear-order/list", method = RequestMethod.GET, name = "根据搜索条件来搜索收款单")
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
                overDue(s.getDueDate().compareTo(DateUtils.getTodayAccurateToDay()) > 0).
                status(s.getStatus()).
                createdAt(s.getCreatedAt()).
                createdById(s.getCreatedBy()).
                createdByName(userService.getUser(s.getCreatedBy()).getName()).
                build();
            result.add(targetVO);
        });

        // 对列表进行多级排序，先按照状态排序，未收款和部分收款的单据排在前面，接着是已确认（收款完成）的，最后是已废弃的单据
        // 第二级是按照duedate升序排列，就是越快要到期了的帐，放在越前面。
        Comparator<ArrearOrderListContentVO> byStatus = Comparator.comparing(ArrearOrderListContentVO::getStatus);
        Comparator<ArrearOrderListContentVO> byDueDate = Comparator.comparing(ArrearOrderListContentVO::getDueDate);
        result.sort(byStatus.thenComparing(byDueDate));
        return ResponseResult.ok(ListResponseUtils
            .generateResponse(new PageImpl<>(result, pageable, page.getTotalElements()), pageIndex, pageSize));
    }

    /**
     * 获取欠款统计列表
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/overdue-warning", method = RequestMethod.GET, name = "获取欠款统计列表")
    public ResponseResult<OverdueCashVO> getOverdueCash() {
        return ResponseResult.ok(arrearOrderService.getOverdueCash());
    }

}
