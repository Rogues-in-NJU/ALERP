package edu.nju.alerp.repo;


import edu.nju.alerp.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 权限用户关联repo
 * @Author: yangguan
 * @CreateDate: 2020-01-08 16:52
 */
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer>, JpaSpecificationExecutor<AuthUser> {
}
