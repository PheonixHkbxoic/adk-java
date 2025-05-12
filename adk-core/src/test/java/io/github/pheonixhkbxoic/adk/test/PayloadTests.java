package io.github.pheonixhkbxoic.adk.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pheonixhkbxoic.adk.AdkUtil;
import io.github.pheonixhkbxoic.adk.message.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/12 22:56
 * @desc
 */
@Slf4j
public class PayloadTests {

    @Test
    public void testPayload() {
        AdkPayload payload = AdkPayload.builder()
                .userId("1")
                .sessionId("2")
                .taskId(AdkUtil.uuid4hex())
                .messages(List.of(
                        AdkTextMessage.of("hello"),
                        AdkVideoMessage.of(MimeType.VIDEO_AVI, new byte[]{1, 2, 3}),
                        AdkImageMessage.of(MimeType.IMAGE_PNG, "abc"),
                        AdkFileMessage.of(MimeType.PDF, new byte[]{}, Map.of("kk", "vv"))
                ))
                .build();
        ObjectMapper om = new ObjectMapper();
        try {
            String json = om.writeValueAsString(payload);
            AdkPayload resultPayload = om.readValue(json, AdkPayload.class);
            log.info("resultPayload: {}", resultPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
}
