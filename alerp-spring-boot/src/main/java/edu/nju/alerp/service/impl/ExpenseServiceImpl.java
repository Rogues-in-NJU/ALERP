package edu.nju.alerp.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.nju.alerp.dto.ExpenseDTO;
import edu.nju.alerp.repo.ExpenseRepository;
import edu.nju.alerp.service.ExpenseService;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.Expense;
import edu.nju.alerp.util.ListResponseUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luhailong
 * @date 2019/12/21
 */
@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public boolean addExpense(ExpenseDTO expenseDTO) {
        Expense expense = Expense.builder().createdAt(sdf.format(new Date())).build();
        BeanUtils.copyProperties(expenseDTO, expense);
        expenseRepository.save(expense);
        return true;
    }

    @Override
    public boolean deleteExpense(int id, int delete_by) {
        Expense expense = Expense.builder().id(id).
            deletedBy(delete_by).
            deletedAt(sdf.format(new Date())).
            build();
        expenseRepository.delete(expense);
        return true;
    }

    @Override
    public List<Expense> getExpenseList() {
        return expenseRepository.findAll();
    }

    @Override
    public ListResponse getExpenseList(int pageIndex, int pageSize) {
        List<Expense> expenseList = expenseRepository.findAll();
        return ListResponseUtils.getListResponse(expenseList,pageIndex,pageSize);
    }
}
