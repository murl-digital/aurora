package digital.murl.aurora;

import digital.murl.aurora.agents.Agent;

import java.util.HashMap;
import java.util.Map;

public class TestAgent extends Agent {
    @Override
    public void init(HashMap<Object, Object> params) {

    }

    @Override
    public HashMap<String, Action> getActions() {
        HashMap<String, Action> actions = new HashMap<>();
        actions.put("test", (a, p) -> System.out.println("lmao"));
        return actions;
    }

    @Override
    public void cleanup() {

    }
}
