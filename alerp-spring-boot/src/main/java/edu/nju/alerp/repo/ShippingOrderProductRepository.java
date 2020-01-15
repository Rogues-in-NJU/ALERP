package edu.nju.alerp.repo;

import edu.nju.alerp.entity.ShippingOrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description: 出货单-商品关联repo
 * @Author: qianen.yin
 * @CreateDate: 2019-12-24 14:34
 */
public interface ShippingOrderProductRepository extends JpaRepository<ShippingOrderProduct, Integer>, JpaSpecificationExecutor<ShippingOrderProduct> {
    List<ShippingOrderProduct> findAllByShippingOrderId(int shippingOrderId);

    @Query("select distinct s.processingOrderId from ShippingOrderProduct s where s.shippingOrderId = :shippingOrderId")
    List<Integer> findProcessingListByShippingId(@Param("shippingOrderId") int shippingOrderId);

    @Query("select sum(s.cash) from ShippingOrderProduct s where s.productId = :productId and s.deletedAt = null")
    Double getTotalCashByProductId(@Param("productId") int productId);

    @Query("select sum(s.weight) from ShippingOrderProduct s where s.productId = :productId and s.weight > 0 and s.deletedAt = null")
    Double getTotalWeightByProductId(@Param("productId") int productId);
}
