package edu.nju.alerp.service.impl;

import java.util.List;

import com.google.common.collect.Lists;
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
        Expense expense = Expense.builder().
            createdAt(DateUtils.getToday()).
            createdBy(CommonUtils.getUserId()).
            code(documentsIdFactory.generateNextCode(DocumentsType.ARREAR_ORDER, CityEnum.of(CommonUtils.getCity()))).
            city(CommonUtils.getCity()).
            build();
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
        // 先查询出所有支出（数据量较小，暂时不需要考虑内存容量问题）
        List<Expense> expenseList = expenseRepository.findAll();
        // 只返回未删除的公司支出
        List<Expense> targetList = Lists.newArrayList();
        for (Expense expense : expenseList) {
            if (expense.getDeletedAt() == null&&expense.getCity()==CommonUtils.getCity()) {
                targetList.add(expense);
            }
        }
        return ListResponseUtils.getListResponse(targetList, pageIndex, pageSize);
    }

}
