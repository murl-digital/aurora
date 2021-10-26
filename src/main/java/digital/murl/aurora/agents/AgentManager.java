package digital.murl.aurora.agents;

import digital.murl.aurora.Action;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class AgentManager {
    private static final ConcurrentHashMap<Integer, Agent> activeAgents;

    static {
        activeAgents = new ConcurrentHashMap<>();
    }

    public static void executeAgentAction(String agentName, String actionName, HashMap<Object, Object> params) {
        Agent agent;
        try {
            agent = AgentRegistrar.getAgent(agentName);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        if (agent == null) return;

        agent.init(params);
        runAction(actionName, params, agent, AgentRegistrar.getAgentActions(agentName));
    }

    private static void runAction(String actionName, HashMap<Object, Object> params, Agent agent, HashMap<String, Action> actions) {
        // TODO: replace with InvalidArgumentException
        if (actions == null) return;

        if (actions.containsKey(actionName)) {
            actions.get(actionName).apply(agent, params);
        }
    }

    public static void executeAgentAction(int agentId, String actionName, HashMap<Object, Object> params) {
        if (!activeAgents.containsKey(agentId)) return;

        Agent agent = activeAgents.get(agentId);
        runAction(actionName, params, agent, AgentRegistrar.getAgentActions(agent.getClass()));
    }
}
