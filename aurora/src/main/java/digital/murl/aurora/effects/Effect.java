package digital.murl.aurora.effects;

import digital.murl.aurora.Result;

import java.util.Map;

public interface Effect {
    Result init(Map<String, Object> params);

    void cleanup();
}
