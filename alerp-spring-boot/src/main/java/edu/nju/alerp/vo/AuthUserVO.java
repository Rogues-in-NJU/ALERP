package edu.nju.alerp.vo;

import lombok.Data;

@Data
public class AuthUserVO {

    private Integer id;
    private Integer authId;
    private String description;
    private Integer userId;
    private Integer action;
}
