package digital.murl.aurora.agents;

import java.util.Map;

public interface Agent {
    void init(Map<String, Object> params);
    void cleanup();
}
