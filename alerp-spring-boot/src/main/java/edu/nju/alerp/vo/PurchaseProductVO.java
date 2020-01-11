package edu.nju.alerp.vo;

import edu.nju.alerp.entity.ProcessOrderProduct;
import edu.nju.alerp.entity.PurchaseOrderProduct;
import lombok.Data;

/**
 * @Description: 采购单商品关联VO
 * @Author: yangguan
 * @CreateDate: 2019-12-26 14:42
 */
@Data
public class PurchaseProductVO {

    private Integer id;
    private Integer productId;
    private String name;
    private Integer quantity;
    private Double weight;
    private Double price;
    private Integer priceType;
    private Double cash;

    public static PurchaseProductVO buildVO(PurchaseOrderProduct product, String name) {
        if (product == null)
            return null;
        PurchaseProductVO purchaseProductVO = new PurchaseProductVO();
        purchaseProductVO.setId(product.getId());
        purchaseProductVO.setProductId(product.getProductId());
        purchaseProductVO.setName(name);
        purchaseProductVO.setQuantity(product.getQuantity());
        purchaseProductVO.setWeight(product.getWeight());
        purchaseProductVO.setPrice(product.getPrice());
        purchaseProductVO.setPriceType(product.getPriceType());
        purchaseProductVO.setCash(product.getCach());
        return purchaseProductVO;
    }
}
