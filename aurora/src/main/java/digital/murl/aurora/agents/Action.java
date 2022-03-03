package digital.murl.aurora.agents;

import java.util.Map;

@FunctionalInterface
public interface Action<T extends Agent, H extends Map<String, Object>> {
    void apply(T agent, H params);
}
