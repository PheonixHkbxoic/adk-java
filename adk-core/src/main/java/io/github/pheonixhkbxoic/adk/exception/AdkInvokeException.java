package io.github.pheonixhkbxoic.adk.exception;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/15 18:23
 * @desc
 */
public class AdkInvokeException extends AdkException {
    public AdkInvokeException(String message) {
        super(message);
    }

    public AdkInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdkInvokeException(Throwable cause) {
        super(cause);
    }
}
