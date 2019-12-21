package edu.nju.alerp.Repo;

import edu.nju.alerp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description: 用户dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 21:32
 */
public interface UserRepository extends JpaRepository<User,Integer> {
}
