package edu.nju.alerp.common.ConditionSqlQuery;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class QueryContainer<T> implements Specification<T> {

    private List<Condition> conditions = new ArrayList<>();

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (!conditions.isEmpty()) {
            List<Predicate> predicates = new ArrayList<>();
            conditions.forEach(con -> predicates.add(con.toPredicate(root, criteriaQuery, criteriaBuilder)));
            Predicate[] predicatesArray = new Predicate[predicates.size()];
            return criteriaBuilder.and(predicates.toArray(predicatesArray));
        }
        return criteriaBuilder.conjunction();
    }

    public void add(Condition condition) {
        if (condition != null)
            conditions.add(condition);
    }
}
