package edu.nju.alerp.vo;


import edu.nju.alerp.entity.Product;
import edu.nju.alerp.entity.PurchaseOrderProduct;
import lombok.Data;

import java.util.List;

/**
 * @Description: 采购单详情VO
 * @Author: yangguan
 * @CreateDate: 2019-12-23 21:33
 */
@Data
public class PurchaseOrderDetailVO {

    private int id;
    private String code;
    private String description;
    private double cash;
    private String salesman;
    private int status;
    private String doneAt;
    private String createdAt;
    private int createdById;
    private String createdByName;
    private List<PurchaseOrderProduct> products;

}
