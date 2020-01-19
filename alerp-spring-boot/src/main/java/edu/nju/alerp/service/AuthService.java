package edu.nju.alerp.service;

import edu.nju.alerp.dto.AuthDTO;
import edu.nju.alerp.dto.UpdateUserAuthDTO;
import edu.nju.alerp.entity.Auth;
import edu.nju.alerp.entity.AuthUser;
import edu.nju.alerp.vo.AuthUserVO;

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

    public AuthUser findAuthUser(int userId, int authId);

    public int updateUserAuth(List<UpdateUserAuthDTO> updateAuths);

    public int initialUserAuthByUserId(int id);

    public List<AuthUserVO> queryAuthUserByUserId(int userId);

    public int initialAuthResource();
}
