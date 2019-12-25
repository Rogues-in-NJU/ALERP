package edu.nju.alerp.repo;

import edu.nju.alerp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Description: 用户dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 21:32
 */
public interface UserRepository extends JpaRepository<User,Integer>,JpaSpecificationExecutor<User> {
    User findDistinctByPhoneNumber(String phoneNumber);
}
