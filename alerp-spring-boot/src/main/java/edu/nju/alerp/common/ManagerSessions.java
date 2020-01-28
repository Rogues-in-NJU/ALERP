package edu.nju.alerp.common;

import io.swagger.models.auth.In;
import lombok.Data;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: session判断
 * @Author: qianen.yin
 * @CreateDate: 2020-01-22 19:53
 */
@Data
public class ManagerSessions {
    private Map<Integer, HttpSession> sessions = new HashMap<Integer, HttpSession>();

    private Map<Integer, String> sessionIds = new HashMap<Integer, String>();

}