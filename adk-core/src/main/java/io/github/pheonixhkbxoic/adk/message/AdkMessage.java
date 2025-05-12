package io.github.pheonixhkbxoic.adk.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.pheonixhkbxoic.adk.core.Adk;

import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 21:01
 * @desc
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AdkTextMessage.class, name = AdkMessage.TEXT),
        @JsonSubTypes.Type(value = AdkImageMessage.class, name = AdkMessage.IMAGE),
        @JsonSubTypes.Type(value = AdkVideoMessage.class, name = AdkMessage.VIDEO),
        @JsonSubTypes.Type(value = AdkAudioMessage.class, name = AdkMessage.AUDIO),
        @JsonSubTypes.Type(value = AdkFileMessage.class, name = AdkMessage.OTHER)
})
public interface AdkMessage extends Adk {
    String TEXT = "text";
    String IMAGE = "image";
    String VIDEO = "video";
    String AUDIO = "audio";
    String OTHER = "other";

    String getType();

    String getMimeType();

    String getUrl();

    Map<String, Object> getMetadata();
}
