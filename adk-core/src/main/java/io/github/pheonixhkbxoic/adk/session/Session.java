package io.github.pheonixhkbxoic.adk.session;

import io.github.pheonixhkbxoic.adk.runtime.AdkContext;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 16:16
 * @desc
 */
@Data
public class Session {
    private String sessionId;
    private ConcurrentMap<String, LinkedList<AdkContext>> taskContextChainMap;

    public Session(String sessionId) {
        this.sessionId = sessionId;
        this.taskContextChainMap = new ConcurrentHashMap<>();
    }

    public void updateSession(String taskId, AdkContext adkContext) {
        LinkedList<AdkContext> chain = this.getTaskContextChain(taskId);
        boolean exist = chain.stream().anyMatch(c -> c.getId().equalsIgnoreCase(adkContext.getId()));
        if (exist) {
            return;
        }
        chain.add(adkContext);
    }

    public LinkedList<AdkContext> getTaskContextChain(String taskId) {
        return taskContextChainMap.computeIfAbsent(taskId, k -> new LinkedList<>());
    }

    public List<String> getTaskIdList() {
        return new ArrayList<>(taskContextChainMap.keySet());
    }
}
