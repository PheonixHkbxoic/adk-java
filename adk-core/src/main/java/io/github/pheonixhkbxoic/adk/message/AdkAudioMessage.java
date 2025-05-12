package io.github.pheonixhkbxoic.adk.message;

import java.util.HashMap;
import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 21:40
 * @desc
 */
public class AdkAudioMessage extends AdkFileMessage {

    public static AdkAudioMessage of(String mimeType, byte[] data) {
        return of(mimeType, data, new HashMap<>());
    }

    public static AdkAudioMessage of(String mimeType, byte[] data, Map<String, Object> metadata) {
        return of(mimeType, data, 0, 0, metadata);
    }

    public static AdkAudioMessage of(String mimeType, byte[] data, long index, long chunks, Map<String, Object> metadata) {
        AdkAudioMessage message = new AdkAudioMessage();
        message.setType(AdkMessage.AUDIO);
        message.setMimeType(mimeType);
        message.setData(data);
        message.setMetadata(metadata);
        message.setIndex(index);
        message.setChunks(chunks);
        return message;
    }

}
