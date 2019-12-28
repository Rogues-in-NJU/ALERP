package edu.nju.alerp.repo;

import edu.nju.alerp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 用户dao层
 * @Author: qianen.yin
 * @CreateDate: 2019-12-17 21:32
 */
public interface UserRepository extends JpaRepository<User,Integer>,JpaSpecificationExecutor<User> {
    /**
     * 根据电话号码获取用户
     * @param phoneNumber
     * @return
     */
    User findDistinctByPhoneNumber(String phoneNumber);

    /**
     * 根据姓名获取用户
     * @param name
     * @return
     */
    User findDistinctByName(String name);
}
