package edu.nju.alerp.common.conditionSqlQuery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @Description: 条件接口
 * @Author: yangguan
 * @CreateDate: 2019-12-21 19:24
 */
public interface Condition {
    public enum Operator {
        EQ, NE, LIKE, GT, LT, GTE, LTE, AND, OR, BETWEEN, IN, NOT_IN, ISNULL
        // 等于， 不等于， 模糊， 大于， 小于， 大于等于， 小于等于, 存在于, 不存在于, 为空
    }

    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query,
                                 CriteriaBuilder builder);
}
