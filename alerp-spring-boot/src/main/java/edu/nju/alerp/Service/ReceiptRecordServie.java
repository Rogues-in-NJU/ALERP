package edu.nju.alerp.Service;

import edu.nju.alerp.Dto.ReceiptRecordDTO;

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
    boolean addReceiptRecord(ReceiptRecordDTO receiptRecordDTO);
}
