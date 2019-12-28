package edu.nju.alerp.repo;

import edu.nju.alerp.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 付款记录Repo
 * @Author: yangguan
 * @CreateDate: 2019-12-25 22:02
 */
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Integer>, JpaSpecificationExecutor<PaymentRecord> {
}
