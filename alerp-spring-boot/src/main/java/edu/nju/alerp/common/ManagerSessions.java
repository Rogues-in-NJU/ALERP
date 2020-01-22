package edu.nju.alerp.common;

import io.swagger.models.auth.In;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: session判断
 * @Author: qianen.yin
 * @CreateDate: 2020-01-22 19:53
 */
public class ManagerSessions {
    private Map<Integer, HttpSession> sessions = new HashMap<Integer, HttpSession>();

    public Map<Integer, HttpSession> getSessions() {
        return sessions;
    }

    public void setSessions(Map<Integer, HttpSession> sessions) {
        this.sessions = sessions;
    }

}