package digital.murl.aurora;

import com.dslplatform.json.DslJson;
import digital.murl.aurora.agents.Agent;
import digital.murl.aurora.agents.AgentManager;
import digital.murl.aurora.agents.AgentRegistrar;
import digital.murl.aurora.regions.*;
import xyz.tozymc.spigot.api.command.CommandController;

import java.util.HashMap;
import java.util.logging.Logger;

public class Aurora {
    public static Logger logger;
    public static Plugin plugin;
    public static DslJson<Object> dslJson;

    static CommandController commandController;

    public static <T extends Agent> void registerAgent(String name, Class<T> agent) throws Exception {
        AgentRegistrar.addAgent(name, agent);
    }

    public static void executeAgentAction(String agentName, String actionName, HashMap<Object, Object> params) {
        if (!plugin.isEnabled()) return;

        AgentManager.executeAgentAction(agentName, actionName, params);
    }

    public static void registerRegionType(String type,
                                      RegionMapConstructor jsonConstructor,
                                      RegionParameterConstructor parameterConstructor,
                                      RegionParameterCompleter parameterCompleter) {
        Regions.addMapConstructor(type, jsonConstructor);
        Regions.addParameterConstructor(type, parameterConstructor);
        Regions.addParameterCompleter(type, parameterCompleter);
    }
}
