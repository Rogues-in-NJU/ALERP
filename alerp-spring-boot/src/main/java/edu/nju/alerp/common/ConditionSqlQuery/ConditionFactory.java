package edu.nju.alerp.common.ConditionSqlQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件构造器
 * 用于创建条件表达式
 * @Author yangguan
 * @CreateDate: 2019-12-21 20:01
 */
public class ConditionFactory {

    /**
     * 等于
     * */
    public static SimpleCondition equal(String fieldName, Object value) throws Exception {
        if (value == null)
            throw new Exception();
        return new SimpleCondition(fieldName, value, Condition.Operator.EQ);
    }

    /**
     * 不等于
     * */
    public static SimpleCondition notEqual(String fieldName, Object value) throws Exception {
        if (value == null)
            throw new Exception();
        return new SimpleCondition(fieldName, value, Condition.Operator.NE);
    }

    /**
     * 模糊匹配
     * */
    public static SimpleCondition like(String fieldName, String value) throws Exception {
        if (value == null)
            throw new Exception();
        return new SimpleCondition(fieldName, value, Condition.Operator.LIKE);
    }

    /**
     * 大于
     * */
    public static <T extends Comparable> SimpleCondition greatThan(String fieldName, T value) throws Exception{
        if (value == null)
            throw  new Exception();
        return new SimpleCondition(fieldName, value, Condition.Operator.GT);
    }

    /**
     * 小于
     * */
    public static <T extends Comparable> SimpleCondition lessThan(String fieldName, T value) throws Exception {
        if (value == null)
            throw new Exception();
        return new SimpleCondition(fieldName, value, Condition.Operator.LT);
    }

    /**
     * 大于等于
     * */
    public static <T extends Comparable> SimpleCondition greatThanEqualTo(String fieldName, T value) throws Exception {
        if (value == null)
            throw new Exception();
        return new SimpleCondition(fieldName, value, Condition.Operator.GTE);
    }

    /**
     * 小于等于
     * */
    public static <T extends Comparable> SimpleCondition lessThanEqualTo(String fieldName, T value) throws Exception {
        if (value == null)
            throw new Exception();
        return new SimpleCondition(fieldName, value, Condition.Operator.LTE);
    }

    /**
     * 并且
     * */
    public static LogicalCondition and(List<Condition> conditions) throws Exception{
        if (conditions == null)
            throw new Exception();
        return new LogicalCondition(conditions, Condition.Operator.AND);
    }

    /**
     * 或者
     * */
    public static LogicalCondition or(List<Condition> conditions) throws Exception{
        if (conditions == null)
            throw new Exception();
        return new LogicalCondition(conditions, Condition.Operator.OR);
    }
}
