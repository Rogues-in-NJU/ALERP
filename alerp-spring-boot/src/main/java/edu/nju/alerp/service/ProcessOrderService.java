package edu.nju.alerp.service;

import edu.nju.alerp.dto.ProcessingOrderDTO;
import edu.nju.alerp.dto.UpdateProcessProductDTO;
import edu.nju.alerp.entity.ProcessOrderProduct;
import edu.nju.alerp.entity.ProcessingOrder;
import edu.nju.alerp.vo.ProcessingOrderDetailVO;
import edu.nju.alerp.vo.ProcessingOrderListVO;
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

    public Page<ProcessingOrderListVO> findAllByPage(Pageable pageable, String id,
                                                     String customerName, Integer status,
                                                     String createAtStartTime, String createAtEndTime);

    public ProcessingOrderDetailVO findProcessingById(int id);

    public ProcessingOrder getOne(int id);

    public int savaProcessingOrder(ProcessingOrder processingOrder);

    public int addProcessingOrder(ProcessingOrderDTO processingOrderDTO);

    public List<ProcessingOrder> findProcessingsByShipppingId(int id);

    public double queryTotalWeight(String createdAtStartTime, String createdAtEndTime);

    public int queryTotalNum(String createdAtStartTime, String createdAtEndTime);

    /**
     * 新增/修改加工单的商品关联
     * */
    public int addOrUpdateProcessProduct(UpdateProcessProductDTO updateProcessProductDTO);

    /**
     * 删除加工单的商品管理
     * */
    public int deleteProcessProduct(int id) throws Exception;

    /**
     * 打印加工单
     * */
    public int printProcessingOrder(int id) throws Exception;

    /**
     * 废弃加工单
     * */
    public int abandonProcessingOrder(int id) throws Exception;

    List<ProcessOrderProduct> findProductByOrderId(int processOrderId);

    ProcessingOrder getLatestOrder(int customerId);
}
