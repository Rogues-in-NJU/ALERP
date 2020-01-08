package edu.nju.alerp.service;

import edu.nju.alerp.dto.AuthDTO;
import edu.nju.alerp.entity.Auth;
import edu.nju.alerp.entity.AuthUser;

import java.util.List;

/**
 * @Description: 权限资源服务接口类
 * @Author: yangguan
 * @CreateDate: 2020-01-08 17:14
 */
public interface AuthService {

    public List<Auth> findAll();

    public int addOrUpdateAuth(AuthDTO authDTO);

    public Auth findAuthByUri(String uri);

    public boolean findAuthUser(int userId, int authId);
}
