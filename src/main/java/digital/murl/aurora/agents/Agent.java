package digital.murl.aurora.agents;

import digital.murl.aurora.Action;

import java.util.HashMap;

public abstract class Agent {
    public boolean isEphemeral = false;

    public abstract HashMap<String, Action> getActions();
}
