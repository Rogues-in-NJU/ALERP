package edu.nju.alerp.Repo;

import edu.nju.alerp.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author luhailong
 * @date 2019/12/21
 */
public interface ExpenseRepository extends JpaRepository<Expense,Integer> {
}
