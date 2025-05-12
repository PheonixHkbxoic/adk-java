package io.github.pheonixhkbxoic.adk.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 21:11
 * @desc
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class AdkTextMessage extends AdkAbstractMessage {
    protected String text;

    public static AdkTextMessage of(String text) {
        return of(MimeType.TEXT, text);
    }

    public static AdkTextMessage of(String mimeType, String text) {
        AdkTextMessage message = new AdkTextMessage();
        message.setType(AdkMessage.TEXT);
        message.setMimeType(mimeType);
        message.setText(text);
        return message;
    }
}
