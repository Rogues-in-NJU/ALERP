package edu.nju.alerp.repo;

import edu.nju.alerp.entity.SpecialPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @Description: 客户特惠价格dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-18 17:33
 */
public interface SpecialPricesRepository extends JpaRepository<SpecialPrice, Integer> {

    @Query("select s from SpecialPrice s where s.customerId = :customerId and s.deletedAt = null")
    List<SpecialPrice> findByCustomerId(@Param("customerId") int customerId);
}
