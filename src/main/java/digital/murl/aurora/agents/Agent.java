package digital.murl.aurora.agents;

import digital.murl.aurora.Action;

import java.util.HashMap;

public abstract class Agent {
    public boolean isEphemeral = false;

    public abstract HashMap<String, Action> getActions();

    public abstract void init(HashMap<Object, Object> params);
    public abstract void cleanup();
}
