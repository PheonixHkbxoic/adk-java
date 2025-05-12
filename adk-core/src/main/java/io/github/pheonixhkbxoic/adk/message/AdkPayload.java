package io.github.pheonixhkbxoic.adk.message;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/3 23:05
 * @desc
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AdkPayload {
    @NotBlank
    private String userId;
    @NotBlank
    private String sessionId;
    private String taskId;
    private boolean stream;
    @Valid
    @NotEmpty
    private List<AdkMessage> messages;
    private Map<String, Object> metadata;
}
