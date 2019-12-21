package edu.nju.alerp.Controller;

import javax.validation.Valid;

import edu.nju.alerp.Dto.ExpenseDTO;
import edu.nju.alerp.Service.ExpenseService;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
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
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult addExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        expenseService.addExpense(expenseDTO);
        return ResponseResult.ok();
    }

    /**
     * 删除公司支出
     *
     * @param id
     * @param delete_by
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete/{id}/{delete_by}", method = RequestMethod.GET)
    public ResponseResult deleteExpense(@PathVariable(value = "id") int id,
        @PathVariable(value = "delete_by") Integer delete_by) {
        expenseService.deleteExpense(id, delete_by);
        return ResponseResult.ok();
    }

    /**
     * 获取公司支出列表（分页）
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseResult<ListResponse> getExpenseList(@RequestParam(value = "pageIndex") int pageIndex,
        @RequestParam(value = "pageSize") int pageSize) {
        ListResponse listResponse = expenseService.getExpenseList(pageIndex,pageSize);
        return ResponseResult.ok(listResponse);
    }
}
