package edu.nju.alerp.common.ConditionSqlQuery;

import edu.nju.alerp.util.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.criteria.*;

@Data
@AllArgsConstructor
public class SimpleCondition implements Condition{

    private String fieldName;
    private Object value;
    private Operator operator;

    @Override
    @SuppressWarnings("unchecked")
    public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder){
        Path condition = root.get(fieldName);
        switch (operator) {
            case EQ:
                return builder.equal(condition, value);
            case NE:
                return builder.notEqual(condition, value);
            case LIKE:
//                if (!(value instanceof String))
//                    throw new Exception(); //抛String类型错误 exception #todo
                return builder.like(condition, CommonUtils.fuzzyStringSplicing((String)value));
            case LT:
                return builder.lessThan(condition, (Comparable) value);
            case GT:
                return builder.greaterThan(condition, (Comparable) value);
            case LTE:
                return builder.lessThanOrEqualTo(condition, (Comparable) value);
            case GTE:
                return builder.greaterThanOrEqualTo(condition, (Comparable) value);
        }
        return null;
    }
}
