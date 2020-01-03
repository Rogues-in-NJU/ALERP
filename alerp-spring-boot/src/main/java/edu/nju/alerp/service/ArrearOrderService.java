package edu.nju.alerp.service;

import edu.nju.alerp.entity.ArrearOrder;
import edu.nju.alerp.vo.ArrearDetailVO;

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
     * @param arrearOrder
     * @return
     */
    int saveArrearOrder(ArrearOrder arrearOrder);

    /**
     * 修改收款单过期时间
     *
     * @param arrearOrderId
     * @param dueDate
     * @return
     */
    int updateDueDate(int arrearOrderId, String dueDate);

    /**
     * 获取收款单详情(包括所有收款记录)
     *
     * @param id
     * @return
     */
    ArrearDetailVO findArrearDetails(int id);
}
