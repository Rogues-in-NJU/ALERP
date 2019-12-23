package edu.nju.alerp.controller;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 加工单Controller
 * @Author: yangguan
 * @CreateDate: 2019-12-21 17:24
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/process-order")
public class ProcessOrderController {

    @RequestMapping(value = "/list")
    public ResponseResult<ListResponse> findProcessOrderByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                                @RequestParam(value = "pageSize") int pageSize,
                                                                @RequestParam(value = "id") String code,
                                                                @RequestParam(value = "customerName") String name,
                                                                @RequestParam(value = "status") int status,
                                                                @RequestParam(value = "createAtStartTime") String startTime,
                                                                @RequestParam(value = "createAtEndTime") String endTime) {
        return null;
    }
}
