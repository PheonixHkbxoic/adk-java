package io.github.pheonixhkbxoic.adk.message;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 21:25
 * @desc BASE64 image
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class AdkImageMessage extends AdkAbstractMessage {
    @NotBlank
    protected String base64;

    public static AdkImageMessage of(String mimeType, String imageBase64) {
        AdkImageMessage message = new AdkImageMessage();
        message.setType(AdkMessage.IMAGE);
        message.setMimeType(mimeType);
        message.setBase64(imageBase64);
        return message;
    }
}
