package digital.murl.aurora.agents;

import digital.murl.aurora.Plugin;
import digital.murl.aurora.Result;

import java.util.Map;

public class Agents {
    public static Result executeAgentAction(String agentName, String actionName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled())
            return pluginNotLoaded();

        return AgentManager.executeAgentAction(agentName, actionName, params);
    }

    private static Result pluginNotLoaded() {
        return new Result(Result.Outcome.ERROR, "The plugin isn't loaded right now! Slow down, whippersnapper");
    }
}
