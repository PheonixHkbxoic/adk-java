package io.github.pheonixhkbxoic.adk.session;

import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 16:16
 * @desc
 */
public class InMemorySessionService implements SessionService {
    protected Map<String, AppSession> appSessionMap = new HashMap<>();
    protected ReentrantLock lock = new ReentrantLock();

    @Override
    public Mono<Session> addSession(String appName, String userId, String sessionId, Session session) {
        return this.getSession(appName, userId, sessionId)
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    lock.lock();
                    try {
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
                }));
    }

    @Override
    public Mono<Session> getSession(String appName, String userId, String sessionId) {
        lock.lock();
        try {
            AppSession appSession = appSessionMap.get(appName);
            if (appSession == null) {
                return Mono.empty();
            }
            Map<String, UserSession> userSessionMap = appSession.getUserSessionMap();
            if (userSessionMap == null) {
                return Mono.empty();
            }
            UserSession userSession = userSessionMap.get(userId);
            if (userSession == null) {
                return Mono.empty();
            }
            Map<String, Session> sessionMap = userSession.getSessionMap();
            if (sessionMap == null) {
                return Mono.empty();
            }
            Session session = sessionMap.get(sessionId);
            return Mono.justOrEmpty(session);
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
