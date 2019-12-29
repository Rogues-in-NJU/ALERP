package edu.nju.alerp.repo;

import edu.nju.alerp.entity.UserCityRelation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 用户城市关联repo
 * @Author: qianen.yin
 * @CreateDate: 2019-12-29 23:03
 */
public interface UserCityRelationRepository extends JpaRepository<UserCityRelation, Integer> {
}
