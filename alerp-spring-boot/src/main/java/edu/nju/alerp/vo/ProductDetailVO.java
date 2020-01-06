package edu.nju.alerp.vo;

import edu.nju.alerp.entity.Product;
import lombok.Data;

@Data
public class ProductDetailVO {

    private Integer id;
    private String name;
    private String shorthand;
    private Integer type;
    private Double density;
    private String specification;
    private String createdAt;
    private Integer createdById;
    private String createdByName;
    private String updatedAt;

    public static ProductDetailVO buildProductDetailVO(Product product, String userName) {
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setId(product.getId());
        productDetailVO.setName(product.getName());
        productDetailVO.setShorthand(product.getShorthand());
        productDetailVO.setType(product.getType());
        productDetailVO.setDensity(product.getDensity());
        productDetailVO.setSpecification(product.getSpecification());
        productDetailVO.setCreatedAt(product.getCreateAt());
        productDetailVO.setCreatedById(product.getCreateBy());
        productDetailVO.setCreatedByName(userName);
        productDetailVO.setUpdatedAt(product.getUpdateAt());
        return productDetailVO;
    }
}
