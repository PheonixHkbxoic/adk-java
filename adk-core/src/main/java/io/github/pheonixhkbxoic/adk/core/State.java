package io.github.pheonixhkbxoic.adk.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/2 00:08
 * @desc
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class State {
    public static String READY = "ready";
    public static String RUNNING = "running";
    public static String SUCCESS = "success";
    public static String FAILURE = "failure";

    private String name;

    public static State of(String stateName) {
        return new State(stateName);
    }

    public boolean eq(String stateName) {
        return this.name.equals(stateName);
    }
}
