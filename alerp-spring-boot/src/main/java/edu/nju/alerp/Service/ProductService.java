package edu.nju.alerp.Service;

import edu.nju.alerp.entity.Product;

import java.util.List;

/**
 * @Description: 商品服务接口
 * @Author: yangguan
 * @CreateDate: 2019-12-19 23:06
 */
public interface ProductService {

    public List<Product> findAll();

    public Product findProductById(int id);

    /**
     * @Description: 可传入Name或者shorthand来进行模糊匹配
     * @CreateDate: 2019-12-19 23:06
     * */
    public List<Product> findProductByCondition(String condition);
}
