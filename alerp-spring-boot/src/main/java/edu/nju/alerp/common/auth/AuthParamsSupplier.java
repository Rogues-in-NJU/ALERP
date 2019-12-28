package edu.nju.alerp.common.auth;


/**
 * @Description: 消费权限接口
 * @Author: yangguan
 * @CreateDate: 2019-12-27 11:55
 */
public interface AuthParamsSupplier {
    void consume(final AuthContext authContext);
}
