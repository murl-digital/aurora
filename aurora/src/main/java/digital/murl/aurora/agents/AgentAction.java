package digital.murl.aurora.agents;

import digital.murl.aurora.Result;

import java.util.Map;

@FunctionalInterface
public interface AgentAction<T extends Agent, H extends Map<String, Object>> {
    Result apply(T agent, H params);
}