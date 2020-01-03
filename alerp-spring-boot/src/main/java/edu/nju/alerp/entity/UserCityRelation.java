package edu.nju.alerp.entity;

/**
 * @Description: 用户城市关联实体类
 * @Author: qianen.yin
 * @CreateDate: 2019-12-29 22:57
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name="user_city_relation")
@NoArgsConstructor
@AllArgsConstructor
public class UserCityRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "city_id")
    private int cityId;
}
