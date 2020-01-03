package edu.nju.alerp.service;

import java.util.List;

import edu.nju.alerp.dto.ReceiptRecordDTO;
import edu.nju.alerp.entity.ReceiptRecord;

/**
 * @author luhailong
 * @date 2019/12/23
 */
public interface ReceiptRecordService {
    /**
     * 新增收款记录
     *
     * @param receiptRecordDTO
     * @return
     */
    int addReceiptRecord(ReceiptRecordDTO receiptRecordDTO);

    /**
     * 删除收款记录
     *
     * @param id
     * @return
     */
    int deleteReceiptRecord(int id);

    /**
     * 根据收款单id和收款记录状态获取收款记录列表,结果按照创建时间升序排序
     *
     * @param arrearId
     * @param status
     * @return
     */
    List<ReceiptRecord> findRecordListByArrearId(int arrearId, int status);
}
