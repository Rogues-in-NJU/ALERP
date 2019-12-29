package edu.nju.alerp.controller;

import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.dto.ReceiptRecordDTO;
import edu.nju.alerp.service.ReceiptRecordServie;
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
    private ReceiptRecordServie receiptRecordServie;

    /**
     * 增加收款记录
     *
     * @param receiptRecordDTO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<Integer> addReceiptRecord(@RequestBody ReceiptRecordDTO receiptRecordDTO) {
        try {
            return ResponseResult.ok(receiptRecordServie.addReceiptRecord(receiptRecordDTO));
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    /**
     * 删除收款记录
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseResult<Integer> deleteReceiptRecord(@PathVariable(value = "id") int id) {
        try {
            return ResponseResult.ok(receiptRecordServie.deleteReceiptRecord(id));
        } catch (NJUException nju) {
            return ResponseResult.fail(nju.getExceptionEnum(), nju.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

}
