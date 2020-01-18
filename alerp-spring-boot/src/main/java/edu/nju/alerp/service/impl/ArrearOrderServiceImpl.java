package edu.nju.alerp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.common.conditionSqlQuery.Condition;
import edu.nju.alerp.common.conditionSqlQuery.ConditionFactory;
import edu.nju.alerp.common.conditionSqlQuery.QueryContainer;
import edu.nju.alerp.dto.ArrearOrderDueDateDTO;
import edu.nju.alerp.dto.ArrearOrderInvoiceNumberDTO;
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
import edu.nju.alerp.service.CustomerService;
import edu.nju.alerp.service.ReceiptRecordService;
import edu.nju.alerp.service.ShippingOrderService;
import edu.nju.alerp.service.UserService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
import edu.nju.alerp.vo.ArrearDetailVO;
import edu.nju.alerp.vo.OverdueCashVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
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
    private CustomerService customerService;

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
    public int updateDueDate(ArrearOrderDueDateDTO dto) {
        ArrearOrder arrearOrder = arrearOrderRepository.getOne(dto.getId());
        if (arrearOrder == null || arrearOrder.getStatus() == ArrearOrderStatus.ABANDONED.getCode()) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "未查到收款单或单据已废弃");
        }
        // 乐观锁控制修改版本
        if (!arrearOrder.getUpdatedAt().equals(dto.getUpdatedAt())) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "客户信息变更，请重新更新！");
        }
        arrearOrder.setDueDate(dto.getDueDate());
        arrearOrder.setUpdatedAt(DateUtils.getToday());
        arrearOrder.setUpdatedBy(CommonUtils.getUserId());
        ArrearOrder result = arrearOrderRepository.save(arrearOrder);
        return result.getId();
    }

    @Override
    public int updateInvoiceNumber(ArrearOrderInvoiceNumberDTO dto) {
        // todo:之后有时间把修改XXX改成一个通用方法，用反射去修改值就可以了
        ArrearOrder arrearOrder = arrearOrderRepository.getOne(dto.getId());
        if (arrearOrder == null || arrearOrder.getStatus() == ArrearOrderStatus.ABANDONED.getCode()) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "未查到收款单或单据已废弃");
        }
        // 乐观锁控制修改版本
        if (!arrearOrder.getUpdatedAt().equals(dto.getUpdatedAt())) {
            throw new NJUException(ExceptionEnum.ILLEGAL_REQUEST, "客户信息变更，请重新更新！");
        }
        arrearOrder.setInvoiceNumber(dto.getInvoiceNumber());
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
            invoiceNumber(arrearOrder.getInvoiceNumber()).
            customerId(customerId).
            receivableCash(arrearOrder.getReceivableCash()).
            receivedCash(arrearOrder.getReceivedCash()).
            dueDate(arrearOrder.getDueDate()).
            status(arrearOrder.getStatus()).
            createdAt(arrearOrder.getCreatedAt()).
            createdById(createdBy).
            updatedAt(arrearOrder.getUpdatedAt()).
            build();
        arrearDetailVO.setCustomerName(customerService.getCustomer(customerId).getName());
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
    public Page<ArrearOrder> getArrearOrderList(Pageable pageable, String code, String customerName, Integer status,
        String invoiceNumber, Integer shippingOrderId, String startTime, String endTime) {
        QueryContainer<ArrearOrder> sp = new QueryContainer<>();
        try {
            sp.add(ConditionFactory.equal("city", CommonUtils.getCity()));

            if (status != null) {
                sp.add(ConditionFactory.equal("status", status));
            }
            if (shippingOrderId != null) {
                sp.add(ConditionFactory.equal("shippingOrderId", shippingOrderId));
            }

            List<Condition> fuzzyMatch = Lists.newArrayList();
            if (Strings.isNotBlank(code)) {
                fuzzyMatch.add(ConditionFactory.like("code", code));
            }
            if (Strings.isNotBlank(customerName)) {
                List<Integer> customerIdList = customerRepository.findCustomerIdByNameAndShorthand(customerName);
                fuzzyMatch.add(ConditionFactory.In("customerId", customerIdList));
            }
            if (Strings.isNotBlank(invoiceNumber)) {
                fuzzyMatch.add(ConditionFactory.like("invoiceNumber", invoiceNumber));
            }
            if (Strings.isNotBlank(startTime)) {
                fuzzyMatch.add(ConditionFactory.greatThanEqualTo("createdAt", startTime));
            }
            if (Strings.isNotBlank(endTime)) {
                fuzzyMatch.add(ConditionFactory.greatThanEqualTo("createdAt", endTime));
            }
            if (!fuzzyMatch.isEmpty()) {
                sp.add(ConditionFactory.or(fuzzyMatch));
            }
        } catch (Exception e) {
            log.error("value is null", e);
        }
        return arrearOrderRepository.findAll(sp, pageable);
    }

    @Override
    public double queryTotalReceivedCash(String startTime, String endTime) {
        QueryContainer<ArrearOrder> arrearOrderSp = new QueryContainer<>();
        double totalReceivedCash = 0.0;
        try {
            // 根据时间范围查询出所有的收款单
            if (Strings.isNotBlank(startTime)) {
                arrearOrderSp.add(ConditionFactory.greatThanEqualTo("doneAt", startTime));
            }
            if (Strings.isNotBlank(endTime)) {
                arrearOrderSp.add(ConditionFactory.greatThanEqualTo("doneAt", endTime));
            }
            // 已废弃的单据不认为参与了金钱交易
            arrearOrderSp.add(ConditionFactory.notEqual("status", ArrearOrderStatus.ABANDONED.getCode()));
            List<ArrearOrder> arrearOrderList = arrearOrderRepository.findAll(arrearOrderSp);
            totalReceivedCash = arrearOrderList.parallelStream().mapToDouble(ArrearOrder::getReceivedCash).sum();
        } catch (Exception e) {
            log.error("Value is null.", e);
        }
        return totalReceivedCash;
    }

    @Override
    public double queryTotalOverdueCash(String startTime, String endTime) {
        QueryContainer<ArrearOrder> arrearOrderSp = new QueryContainer<>();
        double totalReceivedCash = 0.0;
        double totalReceivableCash = 0.0;
        double totalOverdueCash = 0.0;
        try {
            // 根据时间范围查询出所有的收款单
            if (Strings.isNotBlank(startTime)) {
                arrearOrderSp.add(ConditionFactory.greatThanEqualTo("doneAt", startTime));
            }
            if (Strings.isNotBlank(endTime)) {
                arrearOrderSp.add(ConditionFactory.greatThanEqualTo("doneAt", endTime));
            }
            // 已废弃的单据不认为参与了金钱交易
            arrearOrderSp.add(ConditionFactory.notEqual("status", ArrearOrderStatus.ABANDONED.getCode()));
            // 查询所有逾期的收款单
            arrearOrderSp.add(ConditionFactory.greatThanEqualTo("dueDate", DateUtils.getToday()));
            List<ArrearOrder> arrearOrderList = arrearOrderRepository.findAll(arrearOrderSp);
            totalReceivedCash = arrearOrderList.parallelStream().mapToDouble(ArrearOrder::getReceivedCash).sum();
            totalReceivableCash = arrearOrderList.parallelStream().mapToDouble(ArrearOrder::getReceivableCash).sum();
            totalOverdueCash = totalReceivableCash - totalReceivedCash;
        } catch (Exception e) {
            log.error("Value is null.", e);
        }
        return totalOverdueCash;
    }

    @Override
    public OverdueCashVO getOverdueCash() {
        // 按人、按月统计
        List<Map<String, Object>> recordSbMonthList = arrearOrderRepository.getOverdueCashBySbByMonth();
        OverdueCashVO targetVo = new OverdueCashVO();
        Map<String, Object> statistics = Maps.newHashMap();
        // customerList里的一个元素
        Map<String, Object> customerMap = Maps.newHashMap();
        for (Map<String, Object> record : recordSbMonthList) {
            // 读取数据库返回结果的非第一行
            if (customerMap.get("customerId") != null) {
                // 如果是同一个客户的欠款统计，就把月份放置到overdues列表里
                if ((int)customerMap.get("customerId") == ((int)record.get("customerId"))) {
                    List<Map<String, Object>> overdueList = (List<Map<String, Object>>)customerMap.get("overdues");
                    overdueList.add(new HashMap<String, Object>(2) {{
                        put("month", record.get("month"));
                        put("cash", record.get("cash"));
                    }});
                    customerMap.put("overdues", overdueList);
                } else {
                    // 如果是不同客户的欠款统计，就先把整理好的customerMap放到targetVo里，再重新生成一张customerMap
                    customerMap.put("total", getTotalOverduesForSB(customerMap));
                    targetVo.getCustomers().add(customerMap);
                    // 重新生成一张map
                    customerMap = Maps.newHashMap();
                    customerMap.putAll(buildCustomerMap(record));
                }
            } else {
                // 读取数据库返回结果的第一行
                customerMap.putAll(buildCustomerMap(record));
            }
        }
        customerMap.put("total", getTotalOverduesForSB(customerMap));
        targetVo.getCustomers().add(customerMap);

        //按照月份统计
        List<Map<String, Object>> recordMonthList = arrearOrderRepository.getOverdueCashByMonth();
        targetVo.getStatistics().put("overdues", recordMonthList);
        targetVo.getStatistics().put("total",getTotalOverdues(recordMonthList));
        return targetVo;
    }

    /**
     * 构建customerMap，放到customerList中
     *
     * @param record
     * @return
     */
    private Map<String, Object> buildCustomerMap(Map<String, Object> record) {
        Map<String, Object> customerMap = Maps.newHashMap();
        int customerId = (int)record.get("customerId");
        customerMap.put("customerId", customerId);
        customerMap.put("customerName", customerRepository.getOne(customerId).getName());

        List<Map<String, Object>> overdueList = Lists.newArrayList();
        overdueList.add(new HashMap<String, Object>(2) {{
            put("month", record.get("month"));
            put("cash", record.get("cash"));
        }});
        customerMap.put("overdues", overdueList);
        return customerMap;
    }

    /**
     * 获取欠款总额
     *
     * @param recordMonthList
     * @return
     */
    public Double getTotalOverdues(List<Map<String, Object>> recordMonthList) {
        Double result = 0.0;
        for (Map<String, Object> overdueMap : recordMonthList) {
            result += (Double)overdueMap.get("cash");
        }
        return result;
    }

    /**
     * 获取某一个人的欠款总额
     *
     * @param customerMap
     * @return
     */
    public Double getTotalOverduesForSB(Map<String, Object> customerMap) {
        Double result = 0.0;
        List<Map<String, Object>> overdueList = (List<Map<String, Object>>)customerMap.get("overdues");
        for (Map<String, Object> overdueMap : overdueList) {
            result += (Double)overdueMap.get("cash");
        }
        return result;
    }

}
