package edu.nju.alerp.Controller;


import edu.nju.alerp.Dto.ProductDTO;
import edu.nju.alerp.Service.ProductService;
import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.common.ResponseResult;
import edu.nju.alerp.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

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

    @RequestMapping(value = "/list")
    public ResponseResult<ListResponse> findProductsByPages(@RequestParam(value = "pageIndex") int pageIndex,
                                                            @RequestParam(value = "pageSize") int pageSize,
                                                            @RequestParam(value = "name") String name,
                                                            @RequestParam(value = "type") int type) {
        return ResponseResult.ok(productService.findAllByPage(pageIndex, pageSize, name, type));
    }

    @RequestMapping(value = "/{id}")
    public ResponseResult<Product> findProductsDetail(@NotNull(message = "id不能为空") @PathVariable("id") Integer id) {
        return ResponseResult.ok(productService.findProductById(id));
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void addOrUpdateProduct(@RequestBody ProductDTO productDTO) {

    }
}
