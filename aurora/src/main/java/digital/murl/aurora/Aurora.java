package digital.murl.aurora;

import digital.murl.aurora.agents.Agent;
import digital.murl.aurora.agents.AgentAction;
import digital.murl.aurora.agents.AgentManager;
import digital.murl.aurora.effects.Effect;
import digital.murl.aurora.effects.EffectAction;
import digital.murl.aurora.effects.EffectRegistrar;
import digital.murl.aurora.effects.CacheBehavior;
import digital.murl.aurora.regions.*;
import digital.murl.aurora.regions.distributors.RegionDistributor;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Aurora {
    public static <T extends Effect> void registerEffect(String name, Class<T> effect, HashMap<String, EffectAction> actions, CacheBehavior cacheBehavior, HashMap<String, String> paramSchemas) throws Exception {
        EffectRegistrar.addEffect(name, effect, actions, cacheBehavior, paramSchemas);
    }

    public static void registerAgent(String name, Agent agent, HashMap<String, AgentAction> actions, HashMap<String, String> paramSchemas) {
        AgentManager.registerAgent(name, agent, actions, paramSchemas);
    }

    public static void registerRegionType(String type,
                                          RegionMapConstructor mapConstructor,
                                          RegionParameterConstructor parameterConstructor,
                                          RegionParameterCompleter parameterCompleter) {
        Regions.addMapConstructor(type, mapConstructor);
        Regions.addParameterConstructor(type, parameterConstructor);
        Regions.addParameterCompleter(type, parameterCompleter);
    }

    public static void registerRegionDistributor(String type, RegionDistributor distributor) {
        Regions.addRegionDistributor(type, distributor);
    }

    public static HashMap<String, HashMap<String, String>> getEffectActionSchemas() {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();

        for (String effectName :
            EffectRegistrar.getAllEffectNames()) {
            result.put(effectName, EffectRegistrar.getSchemas(effectName));
        }

        return result;
    }

    public static HashMap<String, HashMap<String, String>> getAgentActionSchemas() {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();

        for (String agentName :
            AgentManager.getAllAgentNames()) {
            result.put(agentName, AgentManager.getSchemas(agentName));
        }

        return result;
    }
}
