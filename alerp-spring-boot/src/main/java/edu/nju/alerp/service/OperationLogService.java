package edu.nju.alerp.service;

import edu.nju.alerp.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @Description: 操作日志service层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-25 21:35
 */
public interface OperationLogService {
    int addOperationLog(OperationLog operationLog);

    Page<OperationLog> getOpearationLogList(Pageable pageable, String userName, String operationStartTime, String operationEndTime);
}
