package edu.nju.alerp.dto;

import lombok.Data;

@Data
public class UpdateUserAuthDTO {

    private Integer userId;
    private Integer authId;
    private Integer actionType;

}
