package digital.murl.aurora.agents;

import digital.murl.aurora.Aurora;

import java.util.Map;

@SuppressWarnings("unused")
public class Agents {
    public static String executeAgentAction(String agentName, String actionName, Map<String, Object> params) {
        if (!Aurora.plugin.isEnabled()) return null;

        return AgentManager.executeAgentAction(agentName, actionName, params);
    }

    public static String executeAgentAction(String id, String agentName, String actionName, Map<String, Object> params) {
        if (!Aurora.plugin.isEnabled()) return null;

        return AgentManager.executeAgentAction(id, agentName, actionName, params);
    }

    public static String createAgent(String agentName, Map<String, Object> params) {
        if (!Aurora.plugin.isEnabled()) return null;

        return AgentManager.createAgent(agentName, params);
    }

    public static String createAgent(String id, String agentName, Map<String, Object> params) {
        if (!Aurora.plugin.isEnabled()) return null;

        return AgentManager.createAgent(id, agentName, params);
    }
}