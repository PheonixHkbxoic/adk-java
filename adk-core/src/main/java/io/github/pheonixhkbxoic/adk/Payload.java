package io.github.pheonixhkbxoic.adk;

import lombok.Builder;
import lombok.Data;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/3 23:05
 * @desc
 */
@Builder
@Data
public class Payload {
    private String userId;
    private String sessionId;
    private String message;
    private boolean stream;
}
