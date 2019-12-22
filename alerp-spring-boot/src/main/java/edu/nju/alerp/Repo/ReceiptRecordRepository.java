package edu.nju.alerp.Repo;

import edu.nju.alerp.entity.ReceiptRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 收款记录dao层
 *
 * @author luhailong
 * @date 2019/12/23
 */
public interface ReceiptRecordRepository extends JpaRepository<ReceiptRecord, Integer> {
}
