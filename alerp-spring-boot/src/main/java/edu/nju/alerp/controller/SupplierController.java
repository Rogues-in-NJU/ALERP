package edu.nju.alerp.controller;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.dto.SupplierDTO;
import edu.nju.alerp.service.SupplierService;
import edu.nju.alerp.util.ListResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;

/**
 * @Description: 供货商服务Controller
 * @Author: yangguan
 * @CreateDate: 2019-12-25 22:32
 */
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * 获取供货商列表（分页）
     * @param pageIndex
     * @param pageSize
     * @param name
     * @return
     */
    @RequestMapping(value = "/list")
    public ResponseResult<ListResponse> queryPurchaseOrderByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                                  @RequestParam(value = "pageSize") int pageSize,
                                                                  @RequestParam(value = "name") String name){
        return ResponseResult.ok(ListResponseUtils.generateResponse(supplierService.findSuppliersByPage(PageRequest.of(pageIndex, pageSize), name),pageIndex,pageSize));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<Integer> addOrUpdateProcessingProduct(@RequestBody SupplierDTO supplierDTO) {
        return ResponseResult.ok(supplierService.addOrUpdateSupplier(supplierDTO));
    }

    @RequestMapping(value = "/delete/{id}")
    public ResponseResult<Integer> deleteProcessingProduct(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
            return ResponseResult.ok(supplierService.deleteSupplier(id));
    }
}
