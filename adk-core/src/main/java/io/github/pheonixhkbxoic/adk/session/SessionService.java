package io.github.pheonixhkbxoic.adk.session;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 16:15
 * @desc
 */
public interface SessionService {

    Session addSession(String appName, String userId, String sessionId, Session session);

    Session getSession(String appName, String userId, String sessionId);

}
