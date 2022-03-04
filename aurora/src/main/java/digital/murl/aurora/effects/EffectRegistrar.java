package digital.murl.aurora.effects;

import com.google.common.base.Throwables;
import digital.murl.aurora.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class EffectRegistrar {
    private static final ConcurrentHashMap<String, Class<? extends Effect>> effects;
    private static final ConcurrentHashMap<String, CacheBehavior> effectCacheBehaviors;
    private static final ConcurrentHashMap<String, HashMap<String, EffectAction>> effectActions;
    private static final ConcurrentHashMap<String, HashMap<String, String>> effectActionSchemas;

    static {
        effects = new ConcurrentHashMap<>();
        effectCacheBehaviors = new ConcurrentHashMap<>();
        effectActions = new ConcurrentHashMap<>();
        effectActionSchemas = new ConcurrentHashMap<>();
    }

    public static void addEffect(String name, Class<? extends Effect> effect, HashMap<String, EffectAction> actions, CacheBehavior cacheBehavior, HashMap<String, String> paramSchemas) throws Exception {
        effects.put(name, effect);
        effectCacheBehaviors.put(name, cacheBehavior);
        effectActions.put(name, actions);
        effectActionSchemas.put(name, paramSchemas);
    }

    public static Effect getEffect(String name) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (effects.containsKey(name)) {
            return effects.get(name).getDeclaredConstructor().newInstance();
        }

        return null;
    }

    public static HashMap<String, EffectAction> getEffectActions(String agentName) {
        if (effectActions.containsKey(agentName)) {
            return effectActions.get(agentName);
        }

        return new HashMap<>();
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
