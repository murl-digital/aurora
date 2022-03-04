package digital.murl.aurora.agents;

import digital.murl.aurora.Result;
import digital.murl.aurora.effects.EffectAction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentManager {
    private static final ConcurrentHashMap<String, Agent> agents;
    private static final ConcurrentHashMap<String, HashMap<String, AgentAction>> agentActions;
    private static final ConcurrentHashMap<String, HashMap<String, String>> agentActionSchemas;

    static {
        agents = new ConcurrentHashMap<>();
        agentActions = new ConcurrentHashMap<>();
        agentActionSchemas = new ConcurrentHashMap<>();
    }

    public static void registerAgent(String agentName, Agent agent, HashMap<String, AgentAction> actions, HashMap<String, String> actionSchemas) {
        agents.put(agentName, agent);
        agentActions.put(agentName, actions);
        agentActionSchemas.put(agentName, actionSchemas);
    }

    public static Result executeAgentAction(String agentName, String actionName, Map<String, Object> params) {
        if (!agents.containsKey(agentName))
            return new Result(Result.Outcome.NOT_FOUND, String.format("Agent with name %s doesn't exist", agentName));

        Agent agent = agents.get(agentName);
        Map<String, AgentAction> actions = agentActions.get(agentName);

        if (!actions.containsKey(actionName))
            return new Result(Result.Outcome.INVALID_ARGS, String.format("Action with name %s doesn't exist for agent %s", actionName, agentName));

        return actions.get(actionName).apply(agent, params);
    }

    public static HashMap<String, AgentAction> getAgentActions(String agentName) {
        if (agentActions.containsKey(agentName)) {
            return agentActions.get(agentName);
        }

        return new HashMap<>();
    }

    public static HashMap<String, String> getSchemas(String agentName) {
        return agentActionSchemas.get(agentName);
    }

    public static String[] getAllAgentNames() {
        return agents.keySet().toArray(new String[0]);
    }
}
