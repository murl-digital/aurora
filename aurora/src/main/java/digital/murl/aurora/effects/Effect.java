package digital.murl.aurora.effects;

import java.util.Map;

public interface Effect {
    void init(Map<String, Object> params);

    void cleanup();
}
