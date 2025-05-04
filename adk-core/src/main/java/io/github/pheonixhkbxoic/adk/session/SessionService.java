package io.github.pheonixhkbxoic.adk.session;

import reactor.core.publisher.Mono;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 16:15
 * @desc
 */
public interface SessionService {

    Mono<Session> addSession(String appName, String userId, String sessionId, Session session);

    Mono<Session> getSession(String appName, String userId, String sessionId);

}
