package edu.nju.alerp.controller;


import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.dto.AddPaymentRecordDTO;
import edu.nju.alerp.dto.PurchaseOrderDTO;
import edu.nju.alerp.service.PurchaseOrderService;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.vo.PurchaseOrderDetailVO;
import edu.nju.alerp.vo.PurchaseOrderListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @Description: 采购单Controller
 * @Author: yangguan
 * @CreateDate: 2019-12-23 21:12
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 获取采购单列表（分页）
     * @param pageIndex
     * @param pageSize
     * @param id
     * @param status
     * @param doneStartTime
     * @param doneEndTime
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult<ListResponse> queryPurchaseOrderByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                                  @RequestParam(value = "pageSize") int pageSize,
                                                                  @RequestParam(value = "id") String id,
                                                                  @RequestParam(value = "status") int status,
                                                                  @RequestParam(value = "doneStartTime") String doneStartTime,
                                                                  @RequestParam(value = "doneEndTime") String doneEndTime){
        Page<PurchaseOrderListVO> res =  purchaseOrderService.findAllByPage(PageRequest.of(pageIndex - 1, pageSize),id, status, doneStartTime, doneEndTime);
        return ResponseResult.ok(ListResponseUtils.generateResponse(res, pageIndex, pageSize));
    }

    /**
     * 获取采购单详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseResult<PurchaseOrderDetailVO> findProductsDetail(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(purchaseOrderService.findPurchaseById(id));
    }

    /**
     * 新增采购单
     *
     * @param purchaseOrderDTO
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<Integer> addOrUpdateProduct(@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        return ResponseResult.ok(purchaseOrderService.addNewPurchaseOrder(purchaseOrderDTO));
    }

    /**
     * 新增采购单付款记录
     *
     * @param addPaymentRecordDTO
     * @return
     */
    @RequestMapping(value = "/payment-record", method = RequestMethod.POST)
    public ResponseResult<Integer> addOrUpdateProduct(@RequestBody AddPaymentRecordDTO addPaymentRecordDTO) {
        try {
            return ResponseResult.ok(purchaseOrderService.addNewPaymentRecord(addPaymentRecordDTO));
        }catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    /**
     * 废弃采购单付款记录
     * @param id
     * @return
     */
    @RequestMapping(value = "/payment-record/delete/{id}", method = RequestMethod.GET)
    public ResponseResult<Integer> deletePaymentRecord(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(purchaseOrderService.deletePaymentReport(id));
        }catch (Exception e ) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    /**
     * 废弃采购单
     * @param id
     * @return
     */
    @RequestMapping(value = "/abandon/{id}", method = RequestMethod.GET)
    public ResponseResult<Integer> abandonPurchaseOrder(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(purchaseOrderService.abandonPurchaseOrder(id));
        }catch (Exception e ) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }
}
