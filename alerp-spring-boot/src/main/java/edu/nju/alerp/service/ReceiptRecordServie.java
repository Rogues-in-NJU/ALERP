package edu.nju.alerp.service;

import edu.nju.alerp.dto.ReceiptRecordDTO;

/**
 * @author luhailong
 * @date 2019/12/23
 */
public interface ReceiptRecordServie {
    /**
     * 新增收款记录
     * @param receiptRecordDTO
     * @return
     */
    int addReceiptRecord(ReceiptRecordDTO receiptRecordDTO);

    /**
     * 删除收款记录
     * @param id
     * @return
     */
    int deleteReceiptRecord(int id);
}
