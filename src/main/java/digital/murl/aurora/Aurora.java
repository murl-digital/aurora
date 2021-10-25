package digital.murl.aurora;

import com.dslplatform.json.DslJson;
import digital.murl.aurora.agents.Agent;
import digital.murl.aurora.agents.AgentManager;
import xyz.tozymc.spigot.api.command.CommandController;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class Aurora<T> {
    public static Logger logger;
    public static Plugin plugin;
    public static DslJson<Object> dslJson;

    static CommandController commandController;

    public static <T extends Agent> void registerAgent(String name, Class<T> agent) {
        AgentManager.addAgent(name, agent);
    }

    public static void executeAgentAction(String agentName, String actionName, HashMap<Object, Object> params) {
        Agent agent = null;
        try {
            agent = AgentManager.getAgent(agentName);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        HashMap<String, Action> actions = Objects.requireNonNull(agent).getActions();
        if (actions.containsKey(actionName)) {
            actions.get(actionName).apply(agent, params);
        }
    }
}
