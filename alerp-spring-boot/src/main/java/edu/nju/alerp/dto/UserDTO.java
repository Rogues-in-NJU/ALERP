package edu.nju.alerp.dto;

import edu.nju.alerp.entity.Auth;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 用户DTO
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 17:35
 */
@Data
@Builder
public class UserDTO {
    private Integer id;
    private String name;
    private String phoneNumber;
    private String password;
    private String updatedAt;
    private List<Integer> cities;
    private List<UpdateUserAuthDTO> authList;
}
