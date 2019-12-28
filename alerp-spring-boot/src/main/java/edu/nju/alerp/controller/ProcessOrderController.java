package edu.nju.alerp.controller;

import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.dto.ProcessingOrderDTO;
import edu.nju.alerp.dto.UpdateProcessProductDTO;
import edu.nju.alerp.entity.ProcessingOrder;
import edu.nju.alerp.service.ProcessOrderService;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.vo.ProcessingOrderDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @Description: 加工单Controller
 * @Author: yangguan
 * @CreateDate: 2019-12-21 17:24
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/process-order")
public class ProcessOrderController {

    @Autowired
    private ProcessOrderService processOrderService;

    @RequestMapping(value = "/list")
    public ResponseResult<ListResponse> findProcessOrderByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                                @RequestParam(value = "pageSize") int pageSize,
                                                                @RequestParam(value = "id", required = false) String code,
                                                                @RequestParam(value = "customerName", required = false) String name,
                                                                @RequestParam(value = "status", required = false) Integer status, // todo 这里的status是不是必须的
                                                                @RequestParam(value = "createAtStartTime", required = false) String startTime,
                                                                @RequestParam(value = "createAtEndTime", required = false) String endTime) {
        // todo 增加入参空判断
        Page<ProcessingOrder> processingOrders =
                processOrderService.findAllByPage(PageRequest.of(pageIndex, pageSize), code, name, status, startTime, endTime);
        return ResponseResult.ok(ListResponseUtils.generateResponse(processingOrders, pageIndex, pageSize));
    }

    @RequestMapping(value = "/{id}")
    public ResponseResult<ProcessingOrderDetailVO> findProcessingOrderDetail(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(processOrderService.findProcessingById(id));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<Integer> addNewProcessingOrder(@RequestBody ProcessingOrderDTO processingOrderDTO) {
        return ResponseResult.ok(processOrderService.addProcessingOrder(processingOrderDTO));
    }

    @RequestMapping(value = "/product", method = RequestMethod.POST)
    public ResponseResult<Integer> addOrUpdateProcessingProduct(@RequestBody UpdateProcessProductDTO updateProcessProductDTO) {
        return ResponseResult.ok(processOrderService.addOrUpdateProcessProduct(updateProcessProductDTO));
    }

    @RequestMapping(value = "/product/delete/{id}")
    public ResponseResult<Integer> deleteProcessingProduct(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(processOrderService.deleteProcessProduct(id));
        }catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    @RequestMapping(value = "/print/{id}")
    public ResponseResult<Integer> printProcessingOrder(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(processOrderService.printProcessingOrder(id));
        }catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    @RequestMapping(value = "/abandon/{id}")
    public ResponseResult<Integer> abandonProcessingOrder(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(processOrderService.abandonProcessingOrder(id));
        }catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }
}
