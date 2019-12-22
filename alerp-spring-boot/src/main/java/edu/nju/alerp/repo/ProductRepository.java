package edu.nju.alerp.repo;

import edu.nju.alerp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 商品dao层
 * @Author: yangguan
 * @CreateDate: 2019-12-19 14:15
 */
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product>{
}
