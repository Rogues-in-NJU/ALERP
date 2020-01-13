package edu.nju.alerp.repo;

import edu.nju.alerp.entity.UserCityRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description: 用户城市关联repo
 * @Author: qianen.yin
 * @CreateDate: 2019-12-29 23:03
 */
public interface UserCityRelationRepository extends JpaRepository<UserCityRelation, Integer> {

    @Query("select u.cityId from UserCityRelation u where u.userId = :userId")
    List<Integer> findCitiesByUserId(@Param("userId") int userId);

    @Query("select u.userId from UserCityRelation u where u.cityId = :cityid")
    List<Integer> getUserListByCityId(@Param("cityid") int cityid);
}
