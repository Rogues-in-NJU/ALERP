package edu.nju.alerp.repo;

import edu.nju.alerp.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description: 权限资源repo
 * @Author: yangguan
 * @CreateDate: 2020-01-08 16:52
 */
public interface AuthRepository extends JpaRepository<Auth, Integer>, JpaSpecificationExecutor<Auth> {
}
