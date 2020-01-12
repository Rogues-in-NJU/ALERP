package edu.nju.alerp.service.impl;

import java.util.List;

import com.google.common.collect.Lists;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.ReceiptRecordForArrearDTO;
import edu.nju.alerp.entity.ArrearOrder;
import edu.nju.alerp.entity.ReceiptRecord;
import edu.nju.alerp.entity.ShippingOrder;
import edu.nju.alerp.enums.ArrearOrderStatus;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.enums.ReceiptRecordStatus;
import edu.nju.alerp.repo.ArrearOrderRepository;
import edu.nju.alerp.repo.CustomerRepository;
import edu.nju.alerp.service.ArrearOrderService;
import edu.nju.alerp.service.ReceiptRecordService;
import edu.nju.alerp.service.ShippingOrderService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.vo.ArrearDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author luhailong
 * @date 2019/12/28
 */
@Slf4j
@Service
public class ArrearOrderServiceImpl implements ArrearOrderService {

    @Autowired
    private ArrearOrderRepository arrearOrderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReceiptRecordService receiptRecordService;

    @Autowired
    private ShippingOrderService shippingOrderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public ArrearOrder getOne(int id) {
        return arrearOrderRepository.getOne(id);
    }

    @Override
    public int saveArrearOrder(ArrearOrder arrearOrder) {
        ArrearOrder result = arrearOrderRepository.saveAndFlush(arrearOrder);
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
        arrearOrder.setUpdatedBy(CommonUtils.getUserId());
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
        arrearDetailVO.setOverDue(arrearOrder.getDueDate().compareTo(DateUtils.getToday()) > 0);
        // 根据arrearOrderId查询出货单
        ShippingOrder shippingOrder = shippingOrderService.getShippingOrderByArrearOrderId(id);
        arrearDetailVO.setShippingOrderId(shippingOrder.getId());
        arrearDetailVO.setShippingOrderCode(shippingOrder.getCode());

        // 获取收款单中的收款记录列表：(只显示已确认的收款记录，已废弃的不显示)
        List<ReceiptRecordForArrearDTO> targetDTOList = Lists.newArrayList();
        List<ReceiptRecord> recordList = receiptRecordService.findRecordListByArrearId(id,
            ReceiptRecordStatus.CONFIRMED.getCode());
        for (ReceiptRecord record : recordList) {
            ReceiptRecordForArrearDTO dto = ReceiptRecordForArrearDTO.builder().
                id(record.getId()).
                status(record.getStatus()).
                cash(record.getCash()).
                salesman(record.getSalesman()).
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

    @Override
    public Page<ArrearOrder> getArrearOrderList(Pageable pageable, int id, String customerName, int status,
        String invoiceNumber, int shippingOrderId, String startTime, String endTime) {
        QueryContainer<ArrearOrder> container = new QueryContainer<>();
        List<Integer> customerIdlist = customerRepository.findCustomerIdByNameAndShorthand(customerName);
        try {
            container.add(ConditionFactory.equal("id", id));
            container.add(ConditionFactory.equal("city", CommonUtils.getCity()));
            container.add(ConditionFactory.In("customerId", customerIdlist));
            container.add(ConditionFactory.equal("status", status));
            container.add(ConditionFactory.equal("invoiceNumber", invoiceNumber));
            container.add(ConditionFactory.equal("shippingOrderId", shippingOrderId));

            List<Condition> fuzzyMatch = Lists.newArrayList();
            if (!"".equals(customerName)) {
                fuzzyMatch.add(ConditionFactory.like("name", customerName));
                fuzzyMatch.add(ConditionFactory.like("shorthand", customerName));
            }

            fuzzyMatch.add(ConditionFactory.greatThanEqualTo("createdAt", startTime));
            fuzzyMatch.add(ConditionFactory.lessThanEqualTo("createdAt", endTime));
            container.add(ConditionFactory.or(fuzzyMatch));
        } catch (Exception e) {
            log.error("value is null", e);
        }
        return arrearOrderRepository.findAll(container, pageable);
    }

}
