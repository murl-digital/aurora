package digital.murl.aurora.agents;

import digital.murl.aurora.Result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentManager {
    private static final ConcurrentHashMap<String, Agent> agents;
    private static final ConcurrentHashMap<String, Map<String, Action>> agentActions;

    static {
        agents = new ConcurrentHashMap<>();
        agentActions = new ConcurrentHashMap<>();
    }

    public static void registerAgent(String agentName, Agent agent, Map<String, Action> actions) {
        agents.put(agentName, agent);
        agentActions.put(agentName, actions);
    }

    public static Result executeAgentAction(String agentName, String actionName, Map<String, Object> params) {
        if (!agents.containsKey(agentName))
            return new Result(Result.Outcome.NOT_FOUND, String.format("Agent with name %s doesn't exist", agentName));

        Agent agent = agents.get(agentName);
        Map<String, Action> actions = agentActions.get(agentName);

        if (!actions.containsKey(actionName))
            return new Result(Result.Outcome.INVALID_ARGS, String.format("Action with name %s doesn't exist for agent %s", actionName, agentName));

        return actions.get(agentName).apply(agent, params);
    }
}
