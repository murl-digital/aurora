package digital.murl.aurora.effects;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EffectManager {
    private static final ConcurrentHashMap<String, Effect> activeEffects;
    private static final Cache<String, Effect> effectCache;
    private static final RemovalListener<String, Effect> listener = r -> {
        if (r.wasEvicted())
            r.getValue().cleanup();
    };

    static {
        activeEffects = new ConcurrentHashMap<>();
        effectCache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .removalListener(listener)
            .build();
    }

    @Nullable
    public static String createEffect(String id, String effectName, Map<String, Object> params) {
        return putEffect(id, effectName, params);
    }

    @Nullable
    public static String createEffect(String effectName, Map<String, Object> params) {
        return putEffect(generateRandomString(), effectName, params);
    }

    @Nullable
    private static String putEffect(String id, String effectName, Map<String, Object> params) {
        CacheBehavior cacheBehavior = EffectRegistrar.getCacheBehavior(effectName);
        if (cacheBehavior == null)
            return null;

        Effect effect = getInstance(effectName);
        if (effect == null) return null;

        effect.init(params);
        switch (cacheBehavior) {
            case PERSISTENT:
                activeEffects.put(id, effect);
            case NORMAL:
                effectCache.put(id, effect);
        }

        return id;
    }

    public static String executeEffectAction(String effectName, String actionName, Map<String, Object> params) {
        Effect effect = getInstance(effectName);
        Map<String, Action> actions = EffectRegistrar.getEffectActions(effectName);
        if (effect == null || actions == null)
            return null;
        if (!actions.containsKey(actionName))
            return null;

        CacheBehavior cacheBehavior = EffectRegistrar.getCacheBehavior(effectName);

        String id = "";
        effect.init(params);
        Action.Result result = actions.get(actionName).apply(effect, params);

        switch (Objects.requireNonNull(cacheBehavior)) {
            case PERSISTENT:
                id = generateRandomString();
                activeEffects.put(id, effect);
                break;
            case NORMAL:
                id = generateRandomString();
                switch (result) {
                    case DEFAULT:
                    case ACTIVE:
                        activeEffects.put(id, effect);
                        break;
                    case INACTIVE:
                        effectCache.put(id, effect);
                        break;
                }
        }

        return id;
    }

    public static String executeEffectAction(String id, String effectName, String actionName, Map<String, Object> params) {
        Effect effect;
        boolean wasCached = false;
        if (!effectCache.asMap().containsKey(id) && !activeEffects.containsKey(id)) {
            effect = getInstance(effectName);
            if (effect == null)
                return null;
            effect.init(params);
        } else {
            effect = effectCache.getIfPresent(id);
            if (effect == null)
                effect = activeEffects.get(id);
            else
                wasCached = true;
        }

        Map<String, Action> actions = EffectRegistrar.getEffectActions(effectName);
        CacheBehavior cacheBehavior = EffectRegistrar.getCacheBehavior(effectName);
        if (actions == null || !actions.containsKey(actionName))
            return null;
        if (cacheBehavior == null)
            return null;

        Action.Result result = actions.get(actionName).apply(effect, params);

        if (cacheBehavior == CacheBehavior.PERSISTENT) {
            if (wasCached) {
                // this should never happen, but just in case
                effectCache.invalidate(id);
                activeEffects.put(id, effect);
            }

            return id;
        }

        switch (result) {
            case ACTIVE:
                if (wasCached) {
                    effectCache.invalidate(id);
                    activeEffects.put(id, effect);
                } else {
                    activeEffects.put(id, effect);
                }
                break;
            case INACTIVE:
                if (!wasCached) {
                    activeEffects.remove(id);
                    effectCache.put(id, effect);
                } else {
                    effectCache.put(id, effect);
                }
                break;
            case DEFAULT:
                // we don't have to do anything
                break;
        }

        return id;
    }

    @Nullable
    private static Effect getInstance(String agentName) {
        Effect effect;
        try {
            effect = EffectRegistrar.getEffect(agentName);
        } catch (InstantiationException | IllegalAccessException e) {
            Plugin.logger.severe(Throwables.getStackTraceAsString(e));
            return null;
        }

        if (effect == null) {
            Plugin.logger.warning(String.format("Attempted to create an agent of type %s that doesn't exist", agentName));
            return null;
        }
        return effect;
    }

    // https://medium.com/beingcoders/ways-to-generate-random-string-in-java-6d3b1d964c02
    private static String generateRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(targetStringLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
}
