package edu.nju.alerp.vo;

import edu.nju.alerp.entity.Auth;
import edu.nju.alerp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Description: 用户详细信息VO
 * @Author: qianen.yin
 * @CreateDate: 2020-01-12 19:34
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO {
    private Integer id;
    private String name;
    private String phoneNumber;
    private String password;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;
    private int status;
    private List<Integer> cities;
    private Map<String, List<AuthUserVO>> authList;
}
