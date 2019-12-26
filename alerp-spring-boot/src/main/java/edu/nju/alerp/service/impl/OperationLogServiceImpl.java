package edu.nju.alerp.service.impl;

import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.entity.OperationLog;
import edu.nju.alerp.repo.OperationLogRepository;
import edu.nju.alerp.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 作用描述
 * @Author: qianen.yin
 * @CreateDate: 2019-12-25 21:36
 */
@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Autowired
    private OperationLogRepository operationLogRepository;

    @Override
    public int addOperationLog(OperationLog operationLog) {
        return operationLogRepository.save(operationLog).getId();
    }

    @Override
    public Page<OperationLog> getOpearationLogList(Pageable pageable, String userName, String operationStartTime, String operationEndTime) {
        QueryContainer<OperationLog> sp = new QueryContainer<>();
        try {
            List<Condition> fuzzyMatch = new ArrayList<>();
            fuzzyMatch.add(ConditionFactory.like("userName", userName));
            fuzzyMatch.add(ConditionFactory.greatThanEqualTo("operationStartTime", operationStartTime));
            fuzzyMatch.add(ConditionFactory.lessThanEqualTo("operationEndTime", operationEndTime));
            sp.add(ConditionFactory.or(fuzzyMatch));
        } catch (Exception e) {
            log.error("Value is null", e);
        }
        return operationLogRepository.findAll(sp, pageable);
    }
}
