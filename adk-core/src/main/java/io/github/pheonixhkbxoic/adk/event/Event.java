package io.github.pheonixhkbxoic.adk.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/3 01:15
 * @desc
 */
@ToString
@Builder
@Getter
public class Event {
    private String type;
    private String nodeId;
    private String nodeName;
    private boolean stream;
    private boolean complete;

    /**
     * can be null below
     */
    private Throwable error;


    public static String Execute = "execute";
    public static String ROUTE = "route";
    public static String AGENT_INVOKE = "agent_invoke";

    public boolean isExecute() {
        return Execute.equalsIgnoreCase(this.type);
    }

    public boolean isRoute() {
        return ROUTE.equalsIgnoreCase(this.type);
    }

    public boolean isAgent() {
        return AGENT_INVOKE.equalsIgnoreCase(this.type);
    }

}
