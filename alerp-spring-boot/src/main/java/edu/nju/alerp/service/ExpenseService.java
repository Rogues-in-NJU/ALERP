package edu.nju.alerp.service;

import java.util.List;

import edu.nju.alerp.dto.ExpenseDTO;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.Expense;

/**
 * @author luhailong
 * @date 2019/12/21
 */
public interface ExpenseService {
    /**
     * 新增公司支出
     *
     * @param expenseDTO
     * @return
     */
    boolean addExpense(ExpenseDTO expenseDTO);

    /**
     * 删除公司支出
     *
     * @param id
     * @param delete_by
     * @return
     */
    boolean deleteExpense(int id, int delete_by);

    /**
     * 获取公司支出列表
     *
     * @return
     */
    List<Expense> getExpenseList();

    /**
     * 获取公司支出列表(分页)
     * @param pageIndex
     * @param pageSize
     * @return
     */
    ListResponse getExpenseList(int pageIndex, int pageSize);
}
