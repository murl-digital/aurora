package digital.murl.aurora.agents;

import digital.murl.aurora.Plugin;

import java.util.Map;

public class Agents {
    public static void executeAgentAction(String agentName, String actionName, Map<String, Object> params) {
        if (!Plugin.plugin.isEnabled())
            return;

        AgentManager.executeAgentAction(agentName, actionName, params);
    }
}
