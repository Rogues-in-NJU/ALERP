package edu.nju.alerp.service;

import edu.nju.alerp.dto.ArrearOrderDueDateDTO;
import edu.nju.alerp.dto.ArrearOrderInvoiceNumberDTO;
import edu.nju.alerp.entity.ArrearOrder;
import edu.nju.alerp.vo.ArrearDetailVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 收款单service层
 *
 * @author luhailong
 * @date 2019/12/28
 */
public interface ArrearOrderService {

    /**
     * 根据收款单id获取收款单
     *
     * @param id
     * @return
     */
    ArrearOrder getOne(int id);

    /**
     * 保存收款单
     *
     * @param arrearOrder
     * @return
     */
    int saveArrearOrder(ArrearOrder arrearOrder);

    /**
     * 修改收款单过期时间
     * @param dto
     * @return
     */
    int updateDueDate(ArrearOrderDueDateDTO dto);

    /**
     * 修改收款单发票流水号
     * @param dto
     * @return
     */
    int updateInvoiceNumber(ArrearOrderInvoiceNumberDTO dto);

    /**
     * 获取收款单详情(包括所有收款记录)
     *
     * @param id
     * @return
     */
    ArrearDetailVO findArrearDetails(int id);

    /**
     * 根据查询条件查询收款单列表
     * @param pageable
     * @param code
     * @param customerName 客户姓名或简称
     * @param status 状态
     * @param invoiceNumber 发票流水号
     * @param shippingOrderId 对应出货单id
     * @param startTime 创建时间：开始时间
     * @param endTime 创建时间：结束时间
     * @return
     */
    Page<ArrearOrder> getArrearOrderList(Pageable pageable, String code, String customerName, Integer status, String invoiceNumber,
        Integer shippingOrderId, String startTime, String endTime);
}
