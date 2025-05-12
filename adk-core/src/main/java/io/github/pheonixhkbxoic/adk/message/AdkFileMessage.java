package io.github.pheonixhkbxoic.adk.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 21:33
 * @desc
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class AdkFileMessage extends AdkAbstractMessage {
    protected byte[] data;
    protected long index;
    protected long chunks;

    public AdkFileMessage() {
        this.type = AdkMessage.OTHER;
    }

    public static AdkFileMessage of(String mimeType, byte[] data) {
        return of(mimeType, data, new HashMap<>());
    }

    public static AdkFileMessage of(String mimeType, byte[] data, Map<String, Object> metadata) {
        return of(mimeType, data, 0, 0, metadata);
    }

    public static AdkFileMessage of(String mimeType, byte[] data, long index, long chunks, Map<String, Object> metadata) {
        AdkFileMessage message = new AdkFileMessage();
        message.setMimeType(mimeType);
        message.setData(data);
        message.setMetadata(metadata);
        message.setIndex(index);
        message.setChunks(chunks);
        return message;
    }

}
