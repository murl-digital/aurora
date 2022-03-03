package digital.murl.aurora.effects;

import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EffectRegistrar {
    private static final ConcurrentHashMap<String, Class<? extends Effect>> effects;
    private static final ConcurrentHashMap<String, CacheBehavior> effectCacheBehaviors;
    private static final ConcurrentHashMap<String, HashMap<String, Action>> effectActions;
    private static final ConcurrentHashMap<String, HashMap<String, String>> effectActionSchemas;

    static {
        effects = new ConcurrentHashMap<>();
        effectCacheBehaviors = new ConcurrentHashMap<>();
        effectActions = new ConcurrentHashMap<>();
        effectActionSchemas = new ConcurrentHashMap<>();
    }

    public static void addEffect(String name, Class<? extends Effect> effect, HashMap<String, Action> actions, CacheBehavior cacheBehavior, HashMap<String, String> paramSchemas) throws Exception {
        effects.put(name, effect);
        effectCacheBehaviors.put(name, cacheBehavior);
        effectActions.put(name, actions);
        effectActionSchemas.put(name, paramSchemas);
    }

    public static Effect getEffect(String name) throws InstantiationException, IllegalAccessException {
        if (effects.containsKey(name)) {
            return effects.get(name).newInstance();
        }

        return null;
    }

    public static HashMap<String, Action> getEffectActions(String agentName) {
        if (effectActions.containsKey(agentName)) {
            return effectActions.get(agentName);
        }

        return null;
    }

    public static CacheBehavior getCacheBehavior(String agentName) {
        if (effectCacheBehaviors.containsKey(agentName)) {
            return effectCacheBehaviors.get(agentName);
        }

        return null;
    }

    public static HashMap<String, String> getSchemas(String effectName) {
        return effectActionSchemas.get(effectName);
    }

    public static String[] getAllEffectNames() {
        return (String[]) effects.keySet().stream().toArray();
    }
}
