package edu.nju.alerp.service;

import edu.nju.alerp.dto.ProductDTO;
import edu.nju.alerp.entity.Product;
import edu.nju.alerp.vo.ProductDetailVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Description: 商品服务接口
 * @Author: yangguan
 * @CreateDate: 2019-12-19 23:06
 */
public interface ProductService {

    public List<Product> findAll();

    List<Product> findAllByName(String name);

    public Page<ProductDetailVO> findAllByPage(Pageable pageable, String name, Integer type);

    public Product findProductById(int id);

    public String findProductNameById(int id);

    public int addOrUpdate(ProductDTO productDTO);

    public int abandonProduct(int id);

    public ProductDetailVO findProductVO(int id);
}
