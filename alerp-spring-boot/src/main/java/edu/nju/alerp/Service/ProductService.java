package edu.nju.alerp.Service;

import edu.nju.alerp.common.ListResponse;
import edu.nju.alerp.entity.Product;

import java.util.List;

/**
 * @Description: 商品服务接口
 * @Author: yangguan
 * @CreateDate: 2019-12-19 23:06
 */
public interface ProductService {

    public List<Product> findAll();

    public ListResponse findAllByPage(int pageIndex, int pageSize, String name, int type);

    public Product findProductById(int id);
}
