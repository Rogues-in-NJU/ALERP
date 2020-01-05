package edu.nju.alerp.service.impl;

import java.util.List;

import javax.servlet.http.HttpSession;

import edu.nju.alerp.common.DocumentsIdFactory;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.dto.ExpenseDTO;
import edu.nju.alerp.entity.Expense;
import edu.nju.alerp.enums.CityEnum;
import edu.nju.alerp.enums.DocumentsType;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.repo.ExpenseRepository;
import edu.nju.alerp.service.ExpenseService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
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

    @Autowired
    private DocumentsIdFactory documentsIdFactory;

    @Override
    public int addExpense(ExpenseDTO expenseDTO) {
        Expense expense = Expense.builder().createdAt(DateUtils.getToday()).createdBy(CommonUtils.getUserId()).
            code(documentsIdFactory.generateNextCode(DocumentsType.ARREAR_ORDER, CityEnum.of(CommonUtils.getCity())))
            .build();
        BeanUtils.copyProperties(expenseDTO, expense);
        Expense result = expenseRepository.save(expense);
        return result.getId();
    }

    @Override
    public int deleteExpense(int id) {
        Expense expense = expenseRepository.getOne(id);
        if (expense == null) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "未查到该公司支出");
        }
        expense.setDeletedAt(DateUtils.getToday());
        expense.setDeletedBy(CommonUtils.getUserId());
        Expense result = expenseRepository.save(expense);
        return result.getId();
    }

    @Override
    public List<Expense> getExpenseList() {
        return expenseRepository.findAll();
    }

    @Override
    public ListResponse getExpenseList(int pageIndex, int pageSize) {
        List<Expense> expenseList = expenseRepository.findAll();
        return ListResponseUtils.getListResponse(expenseList, pageIndex, pageSize);
    }

}
