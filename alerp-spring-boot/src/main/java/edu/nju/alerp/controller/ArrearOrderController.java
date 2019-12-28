package edu.nju.alerp.controller;

import javax.xml.ws.Response;

import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.dto.ArrearOrderDueDateDTO;
import edu.nju.alerp.service.ArrearOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /**
     * 获取收款单详情
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{id}")
    public ResponseResult<Integer> findArrearDetails(@PathVariable int id){
        try {
            return ResponseResult.ok(arrearOrderService.findArrearDetails(id));
        } catch (NJUException nju){
            return ResponseResult.fail(nju.getExceptionEnum(),nju.getMessage());
        }catch (Exception e){
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    /**
     * 修改收款单截止日期
     *
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/due-date")
    public ResponseResult<Integer> updateDueDate(@RequestBody ArrearOrderDueDateDTO dto) {
        try {
            return ResponseResult.ok(arrearOrderService.updateDueDate(dto.getId(), dto.getDueDate()));
        } catch (NJUException nju) {
            return ResponseResult.fail(nju.getExceptionEnum(), nju.getMessage());
        } catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }

    }

}
