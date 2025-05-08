package io.github.pheonixhkbxoic.adk.session;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 16:16
 * @desc
 */
public class InMemorySessionService implements SessionService {
    protected final Map<String, AppSession> appSessionMap = new HashMap<>();
    protected final ReentrantLock lock = new ReentrantLock();

    @Override
    public Session addSession(String appName, String userId, String sessionId, Session session) {
        lock.lock();
        try {
            Session exist = this.getSession(appName, userId, sessionId);
            if (exist != null) {
                return exist;
            }
            appSessionMap.computeIfAbsent(appName, k -> new AppSession(k, new HashMap<>()));
            AppSession appSession = appSessionMap.get(appName);

            Map<String, UserSession> userSessionMap = appSession.getUserSessionMap();
            userSessionMap.computeIfAbsent(userId, k -> new UserSession(k, new HashMap<>()));
            UserSession userSession = userSessionMap.get(userId);

            userSession.getSessionMap().computeIfAbsent(sessionId, k -> session);
            return session;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Session getSession(String appName, String userId, String sessionId) {
        lock.lock();
        try {
            AppSession appSession = appSessionMap.get(appName);
            if (appSession == null) {
                return null;
            }
            Map<String, UserSession> userSessionMap = appSession.getUserSessionMap();
            if (userSessionMap == null) {
                return null;
            }
            UserSession userSession = userSessionMap.get(userId);
            if (userSession == null) {
                return null;
            }
            Map<String, Session> sessionMap = userSession.getSessionMap();
            if (sessionMap == null) {
                return null;
            }
            return sessionMap.get(sessionId);
        } finally {
            lock.unlock();
        }
    }


    @Getter
    protected static class AppSession {
        protected String appName;
        protected Map<String, UserSession> userSessionMap;

        public AppSession(String appName, Map<String, UserSession> userSessionMap) {
            this.appName = appName;
            this.userSessionMap = userSessionMap;
        }
    }

    @Getter
    protected static class UserSession {
        protected String userId;
        protected Map<String, Session> sessionMap;

        public UserSession(String userId, Map<String, Session> sessionMap) {
            this.userId = userId;
            this.sessionMap = sessionMap;
        }
    }
}
