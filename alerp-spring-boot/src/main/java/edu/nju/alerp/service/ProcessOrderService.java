package edu.nju.alerp.service;

import edu.nju.alerp.entity.ProcessingOrder;
import edu.nju.alerp.vo.ProcessingOrderDetailVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description: 加工单服务接口
 * @Author: yangguan
 * @CreateDate: 2019-12-21 17:48
 */
public interface ProcessOrderService {

    public List<ProcessingOrder> findAll();

    public Page<ProcessingOrder> findAllByPage(Pageable pageable, String id,
                                                   String customerName, Integer status,
                                                   String createAtStartTime, String createAtEndTime);

    public ProcessingOrderDetailVO findProcessingById(int id);
}
