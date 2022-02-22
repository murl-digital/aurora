package digital.murl.aurora.agents;

import com.google.gson.JsonObject;
import digital.murl.aurora.agents.Agent;

import java.util.Map;

@FunctionalInterface
public interface Action<T extends Agent, H extends Map<Object, Object>> {
    /**
     * Applies an action to a given agent
     *
     * @return A boolean to indicate if an agent should be kept or placed in a cache to eventually be destroyed if unused. If true, the agent will not be placed in the cache.
     */
    boolean apply(T agent, H params);
}
