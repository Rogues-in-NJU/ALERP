package edu.nju.alerp.controller;

import javax.validation.Valid;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.ExpenseDTO;
import edu.nju.alerp.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公司支出controller
 *
 * @author luhailong
 * @date 2019/12/21
 */
@Slf4j
@RestController
@Validated
@RequestMapping(value = "/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    /**
     * 新增公司支出
     *
     * @param expenseDTO
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseResult<Integer> addExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        return ResponseResult.ok(expenseService.addExpense(expenseDTO));
    }

    /**
     * 删除公司支出
     *
     * @param id
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResponseResult<Integer> deleteExpense(@PathVariable(value = "id") int id) {
        return ResponseResult.ok(expenseService.deleteExpense(id));
    }

    /**
     * 获取公司支出列表（分页）
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult<ListResponse> getExpenseList(@RequestParam(value = "pageIndex") int pageIndex,
        @RequestParam(value = "pageSize") int pageSize) {
        return ResponseResult.ok(expenseService.getExpenseList(pageIndex, pageSize));
    }
}
