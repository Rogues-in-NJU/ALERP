package edu.nju.alerp.repo;

import edu.nju.alerp.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 操作日志repo
 * @Author: qianen.yin
 * @CreateDate: 2019-12-25 11:13
 */
public interface OperationLogRepository extends JpaRepository<OperationLog, Integer>, JpaSpecificationExecutor<OperationLog> {
}
