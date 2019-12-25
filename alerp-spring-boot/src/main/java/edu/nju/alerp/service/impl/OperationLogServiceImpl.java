package edu.nju.alerp.service.impl;

import edu.nju.alerp.entity.OperationLog;
import edu.nju.alerp.repo.OperationLogRepository;
import edu.nju.alerp.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 作用描述
 * @Author: qianen.yin
 * @CreateDate: 2019-12-25 21:36
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Autowired
    private OperationLogRepository operationLogRepository;

    @Override
    public int addOperationLog(OperationLog operationLog) {
        return operationLogRepository.save(operationLog).getId();
    }
}
