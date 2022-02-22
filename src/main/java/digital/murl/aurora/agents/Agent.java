package digital.murl.aurora.agents;

import java.util.Map;

public abstract class Agent {
    public boolean isEphemeral = false;

    public abstract HashMap<String, Action> getActions();

    public abstract void init(HashMap<Object, Object> params);
    public abstract void cleanup();
}
