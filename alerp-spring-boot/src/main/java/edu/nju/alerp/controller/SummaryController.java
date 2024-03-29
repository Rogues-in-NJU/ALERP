package edu.nju.alerp.controller;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.SummaryInfoDTO;
import edu.nju.alerp.service.SummaryService;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.vo.SummaryInfoVO;
import edu.nju.alerp.vo.SummaryProductInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 汇总信息controller
 *
 * @author luhailong
 * @date 2020/01/12
 */
@Slf4j
@RestController
@Validated
@RequestMapping(value = "/api/summary")
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    /**
     * 获取汇总信息
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.GET, name = "获取汇总信息")
    public ResponseResult<SummaryInfoVO> getSummaryInfo(@RequestParam("startTime") String startTime,
                                                        @RequestParam("endTime") String endTime) {
        SummaryInfoDTO summaryInfoDTO = new SummaryInfoDTO();
        summaryInfoDTO.setStartTime(startTime);
        summaryInfoDTO.setEndTime(endTime);
        return ResponseResult.ok(summaryService.getSummaryInfo(summaryInfoDTO));
    }

    /**
     * 获取商品汇总信息
     *
     * @return
     */
    @InvokeControl
    @ResponseBody
    @RequestMapping(value = "/product", method = RequestMethod.GET, name = "获取商品汇总信息")
    public ResponseResult<ListResponse> getSummaryProductInfo(@RequestParam(value = "pageIndex") int pageIndex,
                                                              @RequestParam(value = "pageSize") int pageSize,
                                                              @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                              @RequestParam(value = "startTime", required = false, defaultValue = "") String startTime,
                                                              @RequestParam(value = "endTime", required = false, defaultValue = "") String endTime) {
        List<SummaryProductInfoVO> summaryProductInfoVOList = summaryService.getSummaryProductInfo(name, startTime, endTime);
        return ResponseResult.ok(ListResponseUtils.getListResponse(summaryProductInfoVOList, pageIndex, pageSize));
    }

}
