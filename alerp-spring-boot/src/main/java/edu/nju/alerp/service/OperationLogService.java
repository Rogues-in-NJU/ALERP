package edu.nju.alerp.service;

import edu.nju.alerp.entity.OperationLog;

/**
 * @Description: 操作日志service层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-25 21:35
 */
public interface OperationLogService {
    public int addOperationLog(OperationLog operationLog);
}
