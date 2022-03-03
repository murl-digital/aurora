package digital.murl.aurora.effects;

import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EffectRegistrar {
    private static final ConcurrentHashMap<String, Class<? extends Effect>> effects;
    private static final ConcurrentHashMap<String, CacheBehavior> effectCacheBehaviors;
    private static final ConcurrentHashMap<String, HashMap<String, Action>> effectActions;

    static {
        effects = new ConcurrentHashMap<>();
        effectCacheBehaviors = new ConcurrentHashMap<>();
        effectActions = new ConcurrentHashMap<>();
    }

    public static void addEffect(String name, Class<? extends Effect> agent, HashMap<String, Action> actions, CacheBehavior cacheBehavior) throws Exception {
        effects.put(name, agent);
        effectCacheBehaviors.put(name, cacheBehavior);
        effectActions.put(name, actions);
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

    public static HashMap<String, Action> getEffectActions(Class<? extends Effect> agentClass) {
        String name = effects
            .entrySet()
            .stream()
            .filter(entry -> Objects.equal(entry.getValue(), agentClass))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse("");

        if (name.equals("")) return null;

        return getEffectActions(name);
    }
}
