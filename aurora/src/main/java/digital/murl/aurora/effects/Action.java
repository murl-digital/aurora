package digital.murl.aurora.effects;

import digital.murl.aurora.Result;

import java.util.Map;

@FunctionalInterface
public interface Action<T extends Effect, H extends Map<String, Object>> {
    /**
     * Applies an action to a given agent
     *
     * @param effect The effect which the action should run on
     * @param params The parameters given with the action by the transport
     * @return A {@code Result} object with the {@code inner} field being an instance of {@code ActionResult}, which indicates what AgentManager should do with the agent.
     * When an agent's CacheBehavior is NORMAL and an action returns {@code Result.ACTIVE}, AgentManager will ensure that the
     * given agent is in the {@code activeAgents} collection, and vice versa. If an agent should not be affected by the outcome
     * of an action, return {@code Action.DEFAULT}
     * This return value is ignored if an agent was registered as {@code PERSISTENT}.
     * If {@code Action.DEFAULT} is returned when creating a new agent and the cache behavior is {@code NORMAL}, the agent will be placed in the activeAgents collection.
     */
    ActionResult apply(T effect, H params);

    class ActionResult extends Result {
        public final ActiveState activeState;

        public ActionResult(Outcome outcome, String message, ActiveState activeState) {
            super(outcome, message);

            this.activeState = activeState;
        }
    }

    enum ActiveState {
        DEFAULT,
        INACTIVE,
        ACTIVE
    }
}
