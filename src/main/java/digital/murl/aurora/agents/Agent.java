package digital.murl.aurora.agents;

import java.util.Map;

public abstract class Agent {
    public abstract void init(Map<String, Object> params);
    public abstract void cleanup();
}
