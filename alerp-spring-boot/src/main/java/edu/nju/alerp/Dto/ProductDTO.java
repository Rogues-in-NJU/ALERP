package edu.nju.alerp.Dto;

import lombok.Data;

@Data
public class ProductDTO {
    private int id;
    private String name;
    private String shorthand;
    private int type;
    private double density;
    private String specification;
}
