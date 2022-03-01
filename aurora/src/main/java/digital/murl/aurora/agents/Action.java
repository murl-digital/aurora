package digital.murl.aurora.agents;

import java.util.Map;

@FunctionalInterface
public interface Action<T extends Agent, H extends Map<String, Object>> {
    /**
     * Applies an action to a given agent
     *
     * @param agent  The agent which the action should run on
     * @param params The parameters given with the action by the transport
     * @return The result enum, which indicates what AgentManager should do with the agent.
     * When an agent's CacheBehavior is NORMAL and an action returns {@code Result.ACTIVE}, AgentManager will ensure that the
     * given agent is in the {@code activeAgents} collection, and vice versa. If an agent should not be affected by the outcome
     * of an action, return {@code Action.DEFAULT}
     * This return value is ignored if an agent was registered as {@code EPHEMERAL} and {@code PERSISTENT}.
     * If {@code Action.DEFAULT} is returned when creating a new agent and the cache behavior is {@code NORMAL}, the agent will be placed in the activeAgents collection.
     */
    Result apply(T agent, H params);

    enum Result {
        DEFAULT,
        INACTIVE,
        ACTIVE
    }
}
