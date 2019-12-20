package edu.nju.alerp.Repo;

import edu.nju.alerp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 商品dao层
 * @Author: yangguan
 * @CreateDate: 2019-12-19 14:15
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
