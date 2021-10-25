package digital.murl.aurora;

import digital.murl.aurora.agents.Agent;

import java.util.Map;

@FunctionalInterface
public interface Action<T extends Agent, H extends Map<Object, Object> > {
    void apply(T agent, H params);
}
