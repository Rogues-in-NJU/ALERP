package edu.nju.alerp.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description: 供货商列表VO
 * @Author: yangguan
 * @CreateDate: 2019-12-25 22:03
 */
@Data
@Builder
public class SupplierListVO {

    private Integer id;
    private String name;
    private String description;
    private String createdAt;
    private Integer createdById;
    private String createdByName;
    private String updateAt;
}
