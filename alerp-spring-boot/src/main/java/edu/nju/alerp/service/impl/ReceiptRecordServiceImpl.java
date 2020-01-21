package edu.nju.alerp.service.impl;

import java.util.List;

import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.dto.ReceiptRecordDTO;
import edu.nju.alerp.entity.ArrearOrder;
import edu.nju.alerp.entity.ReceiptRecord;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.enums.ArrearOrderStatus;
import edu.nju.alerp.enums.ReceiptRecordStatus;
import edu.nju.alerp.enums.ShippingOrderStatus;
import edu.nju.alerp.repo.ReceiptRecordRepository;
import edu.nju.alerp.service.ArrearOrderService;
import edu.nju.alerp.service.ReceiptRecordService;
import edu.nju.alerp.service.ShippingOrderService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static edu.nju.alerp.enums.ExceptionEnum.ILLEGAL_REQUEST;

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

    @Autowired
    private ArrearOrderService arrearOrderService;

    @Autowired
    private ShippingOrderService shippingOrderService;

    @Override
    public int addReceiptRecord(ReceiptRecordDTO dto) {
        ArrearOrder arrearOrder = arrearOrderService.getOne(dto.getArrearOrderId());
        if (arrearOrder == null || arrearOrder.getId() != dto.getArrearOrderId()) {
            throw new NJUException(ILLEGAL_REQUEST, "未查到对应的收款单");
        }
        // 所在收款单"已完成"状态和"已废弃"状态下，均不能再添加收款记录
        if (arrearOrder.getStatus() == ArrearOrderStatus.ABANDONED.getCode() ||
            arrearOrder.getStatus() == ArrearOrderStatus.FINISHED.getCode()) {
            throw new NJUException(ILLEGAL_REQUEST, "所在收款单已收款完成或者已被废弃");
        }
        double receivedCash = arrearOrder.getReceivedCash() + dto.getCash();
        // 收款金额不能超出总的应收金额
        if (receivedCash > arrearOrder.getReceivableCash()) {
            throw new NJUException(ILLEGAL_REQUEST, "收款金额不能超出总应收金额");
        }
        // 如果实收金额小于应收金额，则把收款单状态修改为"部分收款"
        if (receivedCash < arrearOrder.getReceivableCash()) {
            arrearOrder.setStatus(ArrearOrderStatus.PART_COLLECTED.getCode());
        } else {
            // 如果实收金额等于应收金额，则把收款单状态改为"已确认"，表明收款完成
            arrearOrder.setStatus(ArrearOrderStatus.FINISHED.getCode());
        }
        // 每当有一笔收款记录被添加，就把相应地收款单地"实收金额"加一笔
        arrearOrder.setReceivedCash(receivedCash);
        // 收款单记录，DB变更
        arrearOrderService.saveArrearOrder(arrearOrder);
        // 存储收款记录，DB变更
        ReceiptRecord record = ReceiptRecord.builder().
            createdAt(DateUtils.getTodayAccurateToSecond()).
            createdBy(CommonUtils.getUserId()).
            status(ReceiptRecordStatus.CONFIRMED.getCode()).
            build();
        record.setArrearOrderId(dto.getArrearOrderId());
        record.setCash(dto.getCash());
        record.setSalesman(dto.getSalesman());
        record.setDescription(dto.getDescription());
        record.setDoneAt(dto.getDoneAt());
        ReceiptRecord result = receiptRecordRepository.saveAndFlush(record);
        // 收到第一笔款的时候，就把对应的出货单状态改为"已完成"
        List<ReceiptRecord> receiptRecordList = receiptRecordRepository.findAllByArrearOrderId(dto.getArrearOrderId());
        if (!CollectionUtils.isEmpty(receiptRecordList) && receiptRecordList.size() == 1) {
            ShippingOrder shippingOrder = shippingOrderService.getShippingOrderByArrearOrderId(
                record.getArrearOrderId());
            shippingOrder.setStatus(ShippingOrderStatus.FINISHED.getCode());
            shippingOrderService.saveShippingOrder(shippingOrder);
        }

        return result.getId();
    }

    @Override
    public int deleteReceiptRecord(int id) {
        ReceiptRecord record = receiptRecordRepository.getOne(id);
        // 未查到单据或单据已废弃的情况下，都无法删除
        if (record == null || record.getStatus() == ReceiptRecordStatus.ABANDONED.getCode()) {
            throw new NJUException(ILLEGAL_REQUEST, "未查到单据或单据已废弃");
        }

        ArrearOrder arrearOrder = arrearOrderService.getOne(record.getArrearOrderId());
        // 我们认为废弃的单据是不含金钱交易的，因此把废弃的收款记录里的钱扣除
        // 每当有一笔收款记录被删除，就把相应地收款单地"实收金额"减一笔
        double receivedCash = arrearOrder.getReceivedCash() - record.getCash();
        // 实收金额不能小于0
        if (receivedCash < 0) {
            throw new NJUException(ILLEGAL_REQUEST, "系统出错，废弃该收款记录后，所在收款单实收金额小于0");
        }
        // 如果废弃了这条收款记录之后，该收款单下就没有收款记录了，则把收款单状态修改为"未收款"
        if (receivedCash == 0) {
            arrearOrder.setStatus(ArrearOrderStatus.UNCOLLECTED.getCode());
        } else {
            // 如果废弃了这条收款记录之后，该收款单下还有别的收款记录，则把收款单状态修改为"部分收款"
            arrearOrder.setStatus(ArrearOrderStatus.PART_COLLECTED.getCode());
        }

        arrearOrder.setReceivedCash(receivedCash);
        // 收款单DB变更
        arrearOrderService.saveArrearOrder(arrearOrder);

        // 收款记录软删除
        record.setDeletedAt(DateUtils.getTodayAccurateToSecond());
        record.setDeletedBy(CommonUtils.getUserId());
        record.setStatus(ReceiptRecordStatus.ABANDONED.getCode());
        ReceiptRecord result = receiptRecordRepository.save(record);

        // 如果该收款单下没有收款记录了，就把对应的出货单状态改为"已出货"
        if (CollectionUtils.isEmpty(receiptRecordRepository.findAllByArrearOrderId(id))) {
            ShippingOrder shippingOrder = shippingOrderService.getShippingOrderByArrearOrderId(
                record.getArrearOrderId());
            shippingOrder.setStatus(ShippingOrderStatus.SHIPPIED.getCode());
            shippingOrderService.saveShippingOrder(shippingOrder);
        }

        return result.getId();
    }

    @Override
    public List<ReceiptRecord> findRecordListByArrearId(int arrearId) {
        List<ReceiptRecord> recordList = receiptRecordRepository
            .findReceiptRecordsByArrearOrderIdOrderByDoneAt(arrearId);
        return recordList;
    }

}
