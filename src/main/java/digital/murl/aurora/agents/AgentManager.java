package digital.murl.aurora.agents;

import java.util.HashMap;

public class AgentManager {
    private static HashMap<String, Class<? extends Agent>> agents;

    static {
        agents = new HashMap<>();
    }

    public static void addAgent(String name, Class<? extends Agent> agent) {
        agents.put(name, agent);
    }

    public static Agent getAgent(String name) throws InstantiationException, IllegalAccessException {
        if (agents.containsKey(name)) {
            return agents.get(name).newInstance();
        }

        return null;
    }
}
