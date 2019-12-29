package edu.nju.alerp.controller;

import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.ReceiptRecordDTO;
import edu.nju.alerp.service.ReceiptRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收款记录controller
 *
 * @author luhailong
 * @date 2019/12/28
 */
@Slf4j
@RestController
@Validated
@RequestMapping(value = "/api/arrear-order/receipt-record")
public class ReceiptRecordController {

    @Autowired
    private ReceiptRecordService receiptRecordService;

    /**
     * 增加收款记录
     *
     * @param receiptRecordDTO
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<Integer> addReceiptRecord(@RequestBody ReceiptRecordDTO receiptRecordDTO) {
        return ResponseResult.ok(receiptRecordService.addReceiptRecord(receiptRecordDTO));
    }

    /**
     * 删除收款记录
     *
     * @param id
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseResult<Integer> deleteReceiptRecord(@PathVariable(value = "id") int id) {
        return ResponseResult.ok(receiptRecordService.deleteReceiptRecord(id));
    }

}
