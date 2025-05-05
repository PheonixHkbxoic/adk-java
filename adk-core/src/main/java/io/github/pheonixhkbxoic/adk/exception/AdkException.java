package io.github.pheonixhkbxoic.adk.exception;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/5 16:13
 * @desc
 */
public class AdkException extends RuntimeException {
    public AdkException(String message) {
        super(message);
    }

    public AdkException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdkException(Throwable cause) {
        super(cause);
    }
}
