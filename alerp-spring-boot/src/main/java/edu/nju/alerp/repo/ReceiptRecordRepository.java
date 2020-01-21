package edu.nju.alerp.repo;

import java.util.List;

import edu.nju.alerp.entity.ReceiptRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 收款记录dao层
 *
 * @author luhailong
 * @date 2019/12/23
 */
public interface ReceiptRecordRepository extends JpaRepository<ReceiptRecord, Integer>,
    JpaSpecificationExecutor<ReceiptRecord> {

    /**
     * 根据收款单id获取收款记录列表,结果按照创建时间升序排序
     *
     * @param id
     * @return
     */
    List<ReceiptRecord> findReceiptRecordsByArrearOrderIdOrderByDoneAt(int id);

    /**
     * 查询某一收款单下的所有收款记录
     * @param arrearId
     * @return
     */
    List<ReceiptRecord> findAllByArrearOrderId(int arrearId);
}
