package edu.nju.alerp.service.impl;

import java.util.List;

import javax.servlet.http.HttpSession;

import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.dto.ReceiptRecordDTO;
import edu.nju.alerp.entity.ReceiptRecord;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.ReceiptRecordStatus;
import edu.nju.alerp.repo.ReceiptRecordRepository;
import edu.nju.alerp.service.ReceiptRecordService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luhailong
 * @date 2019/12/23
 */
@Service
public class ReceiptRecordServiceImpl implements ReceiptRecordService {

    @Autowired
    private ReceiptRecordRepository receiptRecordRepository;

    @Autowired
    private UserService userService;

    @Override
    public int addReceiptRecord(ReceiptRecordDTO dto) {
        ReceiptRecord record = ReceiptRecord.builder().
            createdAt(DateUtils.getToday()).
            status(ReceiptRecordStatus.CONFIRMED.getCode()).
            build();
        record.setArrearOrderId(dto.getArrearOrderId());
        record.setCash(dto.getCash());
        record.setSalesmanId(userService.getIdFromName(dto.getSalesman()));
        record.setDescription(dto.getDescription());
        record.setDoneAt(dto.getDoneAt());
        ReceiptRecord result = receiptRecordRepository.save(record);
        return result.getId();
    }

    @Override
    public int deleteReceiptRecord(int id) {
        ReceiptRecord record = receiptRecordRepository.getOne(id);
        // 未查到单据或单据已废弃的情况下，都无法删除
        if (record == null || record.getStatus() == ReceiptRecordStatus.ABANDONED.getCode()) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "未查到单据或单据已废弃");
        }
        record.setDeletedAt(DateUtils.getToday());
        record.setDeletedBy(getUserId());
        record.setStatus(ReceiptRecordStatus.ABANDONED.getCode());
        ReceiptRecord result = receiptRecordRepository.save(record);
        return result.getId();
    }

    @Override
    public List<ReceiptRecord> findRecordListByArrearId(int arrearId, int status) {
        List<ReceiptRecord> recordList = receiptRecordRepository.findReceiptRecordsByArrearOrderIdAndStatusOrderByDoneAt(arrearId,
            status);
        return recordList;
    }

    private int getUserId() {
        HttpSession session = CommonUtils.getHttpSession();
        return session.getAttribute("userId") == null ? 0 : (int)session.getAttribute("userId");
    }

}
