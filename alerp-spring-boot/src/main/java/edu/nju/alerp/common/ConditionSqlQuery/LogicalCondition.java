package edu.nju.alerp.common.ConditionSqlQuery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class LogicalCondition implements Condition{

    private List<Condition> conditions;
    private Operator operator;

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder){
        List<Predicate> predicates = new ArrayList<>();
        conditions.forEach(con -> predicates.add(con.toPredicate(root, query, builder)));
        Predicate[] predicateArray = new Predicate[predicates.size()];
        switch (operator) {
            case AND:
                return builder.and(predicates.toArray(predicateArray));
            case OR:
                return builder.or(predicates.toArray(predicateArray));
        }
        return null;
    }
}
