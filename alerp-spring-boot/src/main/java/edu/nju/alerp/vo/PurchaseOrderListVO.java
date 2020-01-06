package edu.nju.alerp.vo;

import edu.nju.alerp.entity.PurchaseOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 采购单列表VO
 * @Author: yangguan
 * @CreateDate: 2019-12-25 21:15
 */
@Data
@NoArgsConstructor
public class PurchaseOrderListVO {

    private Integer id;
    private String code;
    private String description;
    private Integer supplierId;
    private String supplierName;
    private Double cash;
    private String salesman;
    private Integer status;
    private String doneAt;
    private String createdAt;
    private Integer createdById;
    private String createdByName;

    public static PurchaseOrderListVO buildVO(PurchaseOrder purchaseOrder, String supplierName, String createdByName) {
        if (purchaseOrder == null)
            return null;
        PurchaseOrderListVO purchaseOrderListVO = new PurchaseOrderListVO();
        purchaseOrderListVO.setId(purchaseOrder.getId());
        purchaseOrderListVO.setCode(purchaseOrder.getCode());
        purchaseOrderListVO.setDescription(purchaseOrder.getDescription());
        purchaseOrderListVO.setSupplierId(purchaseOrder.getSupplierId());
        purchaseOrderListVO.setSupplierName(supplierName);
        purchaseOrderListVO.setCash(purchaseOrder.getCash());
        purchaseOrderListVO.setSalesman(purchaseOrder.getSalesman());
        purchaseOrderListVO.setStatus(purchaseOrder.getStatus());
        purchaseOrderListVO.setDoneAt(purchaseOrder.getDoneAt());
        purchaseOrderListVO.setCreatedAt(purchaseOrder.getCreateAt());
        purchaseOrderListVO.setCreatedById(purchaseOrder.getCreateBy());
        purchaseOrderListVO.setCreatedByName(createdByName);
        return purchaseOrderListVO;
    }
}
