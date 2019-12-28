package edu.nju.alerp.service;

/**
 * 收款单service层
 * @author luhailong
 * @date 2019/12/28
 */
public interface ArrearOrderService {

    /**
     * 修改收款单过期时间
     * @param arrearOrderId
     * @param dueDate
     * @return
     */
    int updateDueDate(int arrearOrderId,String dueDate);

    /**
     * 获取收款单详情
     * @param id
     * @return
     */
    int findArrearDetails(int id);
}
