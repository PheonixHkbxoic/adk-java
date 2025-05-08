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
    public static String EXECUTING = "executing";
    public static String ROUTING = "routing";
    public static String INVOKING = "invoking";
    public static String SUCCESS = "success";
    public static String FAILURE = "failure";

    private String name;

    public static State of(String stateName) {
        return new State(stateName);
    }

    public boolean eq(String stateName) {
        return this.name.equals(stateName);
    }

    public boolean isReady() {
        return this.eq(READY);
    }

    public boolean isRunning() {
        return !isReady() && !isSuccess() && isFailure();
    }

    public boolean isSuccess() {
        return this.eq(SUCCESS);
    }

    public boolean isFailure() {
        return this.eq(FAILURE);
    }
}
