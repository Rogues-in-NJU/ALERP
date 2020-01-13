package edu.nju.alerp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 密码DTO
 * @Author: qianen.yin
 * @CreateDate: 2020-01-13 13:27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    private String oldPassword;
    private String newPassword;
}
