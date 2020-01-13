package edu.nju.alerp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 用户权限DTO
 * @Author: qianen.yin
 * @CreateDate: 2020-01-13 14:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserDTO {
    private Integer authId;
    private Integer action;
}
