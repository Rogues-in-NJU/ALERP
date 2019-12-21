package edu.nju.alerp.Repo;

import edu.nju.alerp.entity.SpecialPrices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Description: 客户特惠价格dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-18 17:33
 */
public interface SpecialPricesRepository extends JpaRepository<SpecialPrices, Integer> {

    List<SpecialPrices> findByCustomerId(int customerId);
}
