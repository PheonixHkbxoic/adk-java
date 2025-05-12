package io.github.pheonixhkbxoic.adk.message;

import lombok.Data;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 17:07
 * @desc
 */
@Data
public class ResponseFrame {
    private String message;

    public static ResponseFrame of(String message) {
        ResponseFrame r = new ResponseFrame();
        r.setMessage(message);
        return r;
    }
}
