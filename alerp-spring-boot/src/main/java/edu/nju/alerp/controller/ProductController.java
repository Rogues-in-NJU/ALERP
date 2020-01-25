package edu.nju.alerp.controller;


import edu.nju.alerp.common.aop.InvokeControl;
import edu.nju.alerp.dto.ProductDTO;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.util.ListResponseUtils;
import edu.nju.alerp.vo.ProductDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @Description: 商品Controller
 * @Author: yangguan
 * @CreateDate: 2019-12-20 14:06
 */

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    /**
     * 获取商品列表（分页）
     * @param pageIndex
     * @param pageSize
     * @param name
     * @param type
     * @return
     */
    @InvokeControl
    @RequestMapping(value = "/list", method = RequestMethod.GET, name = "获取商品列表（分页）")
    public ResponseResult<ListResponse> findProductsByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                            @RequestParam(value = "pageSize") int pageSize,
                                                            @RequestParam(value = "name", required = false) String name,
                                                            @RequestParam(value = "type", required = false) Integer type) {
        Page<ProductDetailVO> page = productService.findAllByPage(PageRequest.of(pageIndex - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createAt")), name, type);
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }


    /**
     * 获取商品详情
     * @param id
     * @return
     */
    @InvokeControl
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, name = "获取商品详情")
    public ResponseResult<ProductDetailVO> findProductsDetail(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(productService.findProductVO(id));
    }

    /**
     * 新增或更新商品
     *
     * @param productDTO
     * @return
     */
    @InvokeControl
    @RequestMapping(value = "", method = RequestMethod.POST, name = "新增商品/更新商品")
    public ResponseResult<Integer> addOrUpdateProduct(@RequestBody ProductDTO productDTO) {
        try {
            int res = productService.addOrUpdate(productDTO);
            return ResponseResult.ok(res);
        }catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }

    /**
     * 废弃商品
     * @param id
     * @return
     */
    @InvokeControl
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET, name = "废弃商品")
    public ResponseResult<Integer> abandonPurchaseOrder(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        try {
            return ResponseResult.ok(productService.abandonProduct(id));
        }catch (Exception e ) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }
}
