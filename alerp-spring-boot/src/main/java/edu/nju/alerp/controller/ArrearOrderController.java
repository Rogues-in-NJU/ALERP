package edu.nju.alerp.controller;

import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.ArrearOrderDueDateDTO;
import edu.nju.alerp.service.ArrearOrderService;
import edu.nju.alerp.vo.ArrearDetailVO;
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

}
