package edu.nju.alerp.controller;


import edu.nju.alerp.dto.ProductDTO;
import edu.nju.alerp.service.ProductService;
import edu.nju.alerp.common.ExceptionWrapper;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.util.ListResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @RequestMapping(value = "/list")
    public ResponseResult<ListResponse> findProductsByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                            @RequestParam(value = "pageSize") int pageSize,
                                                            @RequestParam(value = "name") String name,
                                                            @RequestParam(value = "type") int type) {
        Page<Product> page = productService.findAllByPage(PageRequest.of(pageIndex - 1, pageSize), name, type);
        return ResponseResult.ok(ListResponseUtils.generateResponse(page, pageIndex, pageSize));
    }


    /**
     * 获取商品详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}")
    public ResponseResult<Product> findProductsDetail(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(productService.findProductById(id));
    }

    /**
     * 新增公司支出
     *
     * @param productDTO
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseResult<Integer> addOrUpdateProduct(@RequestBody ProductDTO productDTO) {
        try {
            int res = productService.addOrUpdate(productDTO);
            return ResponseResult.ok(res);
        }catch (Exception e) {
            return ResponseResult.fail(ExceptionWrapper.defaultExceptionWrapper(e));
        }
    }
}
