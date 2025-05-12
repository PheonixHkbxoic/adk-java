package io.github.pheonixhkbxoic.adk.message;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 21:12
 * @desc
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public abstract class AdkAbstractMessage implements AdkMessage {
    protected String id;
    protected String name;
    @NotBlank
    protected String type;
    @NotBlank
    protected String mimeType;
    protected String url;
    protected Map<String, Object> metadata;


    public static AdkVideoMessage of(String type, String mimeType, String url) {
        return of(type, mimeType, url, new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public static <T extends AdkFileMessage> T of(String type, String mimeType, String url, Map<String, Object> metadata) {
        AdkVideoMessage message = new AdkVideoMessage();
        message.setType(type);
        message.setMimeType(mimeType);
        message.setUrl(url);
        message.setMetadata(metadata);
        return (T) message;
    }
}
