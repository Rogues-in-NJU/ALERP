package edu.nju.alerp.service.impl;

import javax.servlet.http.HttpSession;

import edu.nju.alerp.common.NJUException;
import edu.nju.alerp.entity.ArrearOrder;
import edu.nju.alerp.enums.ArrearOrderStatus;
import edu.nju.alerp.enums.ExceptionEnum;
import edu.nju.alerp.repo.ArrearOrderRepository;
import edu.nju.alerp.service.ArrearOrderService;
import edu.nju.alerp.util.CommonUtils;
import edu.nju.alerp.util.DateUtils;
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
    public int findArrearDetails(int id) {
        //todo：收款单详情
        return 0;
    }

    private int getUserId() {
        HttpSession session = CommonUtils.getHttpSession();
        return session.getAttribute("userId") == null ? 0 : (int)session.getAttribute("userId");
    }
}
