package digital.murl.aurora.agents;

import digital.murl.aurora.Action;

import java.util.HashMap;
import java.util.function.Function;

public abstract class Agent {
    public abstract HashMap<String, Action> getActions();
}
