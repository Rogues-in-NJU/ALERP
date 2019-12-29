package edu.nju.alerp.repo;

import java.util.List;

import edu.nju.alerp.entity.ReceiptRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 收款记录dao层
 *
 * @author luhailong
 * @date 2019/12/23
 */
public interface ReceiptRecordRepository extends JpaRepository<ReceiptRecord, Integer> {

    /**
     * 根据收款单id和收款记录状态获取收款记录列表,结果按照创建时间升序排序
     *
     * @param id
     * @param status
     * @return
     */
    List<ReceiptRecord> findReceiptRecordsByArrearOrderIdAndStatusOrderByDoneAt(int id, int status);
}
