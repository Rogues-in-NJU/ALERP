package edu.nju.alerp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 作用描述
 * @Author: qianen.yin
 * @CreateDate: 2020-01-12 16:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private Integer id;
    private String name;
    private String phoneNumber;
    private Integer status;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
