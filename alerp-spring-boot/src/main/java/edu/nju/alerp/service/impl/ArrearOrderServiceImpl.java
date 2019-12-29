package edu.nju.alerp.service.impl;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.google.common.collect.Lists;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.dto.ReceiptRecordForArrearDTO;
import edu.nju.alerp.entity.ArrearOrder;
import edu.nju.alerp.entity.ReceiptRecord;
import edu.nju.alerp.enums.ArrearOrderStatus;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.ReceiptRecordStatus;
import edu.nju.alerp.repo.ArrearOrderRepository;
import edu.nju.alerp.service.ArrearOrderService;
import edu.nju.alerp.service.ReceiptRecordService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.vo.ArrearDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luhailong
 * @date 2019/12/28
 */
@Service
public class ArrearOrderServiceImpl implements ArrearOrderService {

    @Autowired
    private ArrearOrderRepository arrearOrderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReceiptRecordService receiptRecordService;

    @Override
    public ArrearOrder getOne(int id) {
        return arrearOrderRepository.getOne(id);
    }

    @Override
    public int saveArrearOrder(ArrearOrder arrearOrder) {
        ArrearOrder result = arrearOrderRepository.save(arrearOrder);
        return result.getId();
    }

    @Override
    public int updateDueDate(int arrearOrderId, String dueDate) {
        ArrearOrder arrearOrder = arrearOrderRepository.getOne(arrearOrderId);
        if (arrearOrder == null || arrearOrder.getStatus() == ArrearOrderStatus.ABANDONED.getCode()) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "未查到收款单或单据已废弃");
        }
        arrearOrder.setDueDate(dueDate);
        arrearOrder.setUpdatedAt(DateUtils.getToday());
        arrearOrder.setUpdatedBy(getUserId());
        ArrearOrder result = arrearOrderRepository.save(arrearOrder);
        return result.getId();
    }

    @Override
    public ArrearDetailVO findArrearDetails(int id) {
        ArrearOrder arrearOrder = arrearOrderRepository.getOne(id);
        if (arrearOrder == null) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "未查到收款单");
        }
        int customerId = arrearOrder.getCustomerId();
        int createdBy = arrearOrder.getCreatedBy();
        // 获取收款单的基本信息:
        ArrearDetailVO arrearDetailVO = ArrearDetailVO.builder().
            id(arrearOrder.getId()).
            code(arrearOrder.getCode()).
            customerId(customerId).
            receivableCash(arrearOrder.getReceivableCash()).
            receivedCash(arrearOrder.getReceivedCash()).
            dueDate(arrearOrder.getDueDate()).
            status(arrearOrder.getStatus()).
            createdAt(arrearOrder.getCreatedAt()).
            createdById(createdBy).build();
        arrearDetailVO.setCustomerName(userService.getUser(customerId).getName());
        arrearDetailVO.setCreatedByName(userService.getUser(createdBy).getName());
        // todo:shippingOrderService中提供根据arrearOrderId查询shippingOrderID的方法，然后填到VO中
        arrearDetailVO.setOverDue(arrearOrder.getDueDate().compareTo(DateUtils.getToday()) > 0);

        // 获取收款单中的收款记录列表：(只显示已确认的收款记录，已废弃的不显示)
        List<ReceiptRecordForArrearDTO> targetDTOList = Lists.newArrayList();
        List<ReceiptRecord> recordList = receiptRecordService.findRecordListByArrearId(id,
            ReceiptRecordStatus.CONFIRMED.getCode());
        for (ReceiptRecord record : recordList) {
            ReceiptRecordForArrearDTO dto = ReceiptRecordForArrearDTO.builder().
                id(record.getId()).
                status(record.getStatus()).
                cash(record.getCash()).
                salesman(userService.getUser(record.getSalesmanId()).getName()).
                description(record.getDescription()).
                doneAt(record.getDoneAt()).
                createdAt(record.getCreatedAt()).
                createdById(record.getCreatedBy()).
                createdByName(userService.getUser(record.getCreatedBy()).getName()).build();
            targetDTOList.add(dto);
        }
        arrearDetailVO.setReceiptRecordList(targetDTOList);
        return arrearDetailVO;
    }

    private int getUserId() {
        HttpSession session = CommonUtils.getHttpSession();
        return session.getAttribute("userId") == null ? 0 : (int)session.getAttribute("userId");
    }
}
