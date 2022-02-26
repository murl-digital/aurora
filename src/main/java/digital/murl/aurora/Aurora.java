package digital.murl.aurora;

import com.dslplatform.json.DslJson;
import digital.murl.aurora.agents.*;
import digital.murl.aurora.regions.*;
import digital.murl.aurora.regions.distributors.RegionDistributor;
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

    public static void registerRegionType(String type,
                                      RegionMapConstructor jsonConstructor,
                                      RegionParameterConstructor parameterConstructor,
                                      RegionParameterCompleter parameterCompleter) {
        Regions.addMapConstructor(type, jsonConstructor);
        Regions.addParameterConstructor(type, parameterConstructor);
        Regions.addParameterCompleter(type, parameterCompleter);
    }

    public static void registerRegionDistributor(String type, RegionDistributor distributor) {
        Regions.addRegionDistributor(type, distributor);
    }
}
