package edu.nju.alerp.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Integer id;
    private String name;
    private String shorthand;
    private int type;
    private double density;
    private String specification;
    private String updateTime;
}
