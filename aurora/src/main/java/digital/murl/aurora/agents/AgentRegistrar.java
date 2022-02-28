package digital.murl.aurora.agents;

import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentRegistrar {
    private static final ConcurrentHashMap<String, Class<? extends Agent>> agents;
    private static final ConcurrentHashMap<String, CacheBehavior> agentCacheBehaviors;
    private static final ConcurrentHashMap<String, HashMap<String, Action>> agentActions;

    static {
        agents = new ConcurrentHashMap<>();
        agentCacheBehaviors = new ConcurrentHashMap<>();
        agentActions = new ConcurrentHashMap<>();
    }

    public static void addAgent(String name, Class<? extends Agent> agent, HashMap<String, Action> actions, CacheBehavior cacheBehavior) throws Exception {
        agents.put(name, agent);
        agentCacheBehaviors.put(name, cacheBehavior);
        agentActions.put(name, actions);
    }

    public static Agent getAgent(String name) throws InstantiationException, IllegalAccessException {
        if (agents.containsKey(name)) {
            return agents.get(name).newInstance();
        }

        return null;
    }

    public static HashMap<String, Action> getAgentActions(String agentName) {
        if (agentActions.containsKey(agentName)) {
            return agentActions.get(agentName);
        }

        return null;
    }

    public static CacheBehavior getCacheBehavior(String agentName) {
        if (agentCacheBehaviors.containsKey(agentName)) {
            return agentCacheBehaviors.get(agentName);
        }

        return null;
    }

    public static HashMap<String, Action> getAgentActions(Class<? extends Agent> agentClass) {
        String name = agents
            .entrySet()
            .stream()
            .filter(entry -> Objects.equal(entry.getValue(), agentClass))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse("");

        if (name.equals("")) return null;

        return getAgentActions(name);
    }
}
