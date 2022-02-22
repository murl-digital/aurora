package digital.murl.aurora;

import com.dslplatform.json.DslJson;
import digital.murl.aurora.agents.*;
import xyz.tozymc.spigot.api.command.CommandController;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Aurora {
    public static Logger logger;
    public static Plugin plugin;
    public static DslJson<Object> dslJson;

    static CommandController commandController;

    public static <T extends Agent> void registerAgent(String name, Class<T> agent, HashMap<String, Action> actions, CacheBehavior cacheBehavior) throws Exception {
        AgentRegistrar.addAgent(name, agent, actions, cacheBehavior);
    }

    public static String executeAgentAction(String agentName, String actionName, Map<String, Object> params) {
        if (!plugin.isEnabled()) return null;

        return AgentManager.executeAgentAction(agentName, actionName, params);
    }

    public static String executeAgentAction(String id, String agentName, String actionName, Map<String, Object> params) {
        if (!plugin.isEnabled()) return null;

        return AgentManager.executeAgentAction(agentName, actionName, params);
    }
}
