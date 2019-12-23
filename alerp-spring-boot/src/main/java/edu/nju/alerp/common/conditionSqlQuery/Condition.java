package edu.nju.alerp.common.conditionSqlQuery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface Condition {
    public enum Operator {
        EQ, NE, LIKE, GT, LT, GTE, LTE, AND, OR
        // 等于， 不等于， 模糊， 大于， 小于， 大于等于， 小于等于
    }
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder builder);
}