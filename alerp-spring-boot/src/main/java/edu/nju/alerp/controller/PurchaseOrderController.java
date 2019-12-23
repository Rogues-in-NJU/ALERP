package edu.nju.alerp.controller;


import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 采购单Controller
 * @Author: yangguan
 * @CreateDate: 2019-12-23 21:12
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/purchase-order")
public class PurchaseOrderController {

    /**
     * 获取采购单列表（分页）
     * @param pageIndex
     * @param pageSize
     * @param id
     * @param status
     * @param doneStartTime
     * @param doneEndTime
     * @return
     */
    @RequestMapping(value = "/list")
    public ResponseResult<ListResponse> queryPurchaseOrderByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                                  @RequestParam(value = "pageSize") int pageSize,
                                                                  @RequestParam(value = "id") String id,
                                                                  @RequestParam(value = "status") int status,
                                                                  @RequestParam(value = "doneStartTime") String doneStartTime,
                                                                  @RequestParam(value = "doneEndTime") String doneEndTime){
        return null;
    }
}
