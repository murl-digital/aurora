package digital.murl.aurora.effects;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import digital.murl.aurora.Plugin;
import digital.murl.aurora.Result;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
            .expireAfterAccess(Plugin.config.getInt("effects.cacheDuration"), TimeUnit.SECONDS)
            .removalListener(listener)
            .build();
    }

    public static Result createEffect(String id, String effectName, Map<String, Object> params) {
        return putEffect(id, effectName, params);
    }

    public static Result createEffect(String effectName, Map<String, Object> params) {
        return putEffect(generateRandomString(), effectName, params);
    }

    public static String[] getAllEffectInstances() {
        List<String> effects = new LinkedList<>();
        effects.addAll(activeEffects.keySet());
        effects.addAll(effectCache.asMap().keySet());
        return (String[]) effects.stream().toArray();
    }

    private static Result putEffect(String id, String effectName, Map<String, Object> params) {
        CacheBehavior cacheBehavior = EffectRegistrar.getCacheBehavior(effectName);
        if (cacheBehavior == null)
            return new Result(Result.Outcome.NOT_FOUND, String.format("No effect with name %s exists", effectName));

        Effect effect = getInstance(effectName);
        if (effect == null)
            return new Result(Result.Outcome.NOT_FOUND, String.format("No effect with name %s exists", effectName));

        Result result = effect.init(params);

        if (result.outcome != Result.Outcome.SUCCESS)
            return result;

        switch (cacheBehavior) {
            case PERSISTENT:
                activeEffects.put(id, effect);
            case NORMAL:
                effectCache.put(id, effect);
        }

        return new Result(Result.Outcome.SUCCESS, id);
    }

    public static Result executeEffectAction(String id, String effectName, String actionName, Map<String, Object> params) {
        Effect effect;
        boolean wasCached = false;
        if (!effectCache.asMap().containsKey(id) && !activeEffects.containsKey(id))
            return new Result(Result.Outcome.NOT_FOUND, String.format("No effect with is %s currently exists", id));
        effect = effectCache.getIfPresent(id);
        if (effect == null)
            effect = activeEffects.get(id);
        else
            wasCached = true;

        Map<String, EffectAction> actions = EffectRegistrar.getEffectActions(effectName);
        CacheBehavior cacheBehavior = EffectRegistrar.getCacheBehavior(effectName);
        if (actions == null)
            return new Result(Result.Outcome.INVALID_ARGS, String.format("No actions exist for effect %s. Is the effect name correct?", effectName));
        if (!actions.containsKey(actionName))
            return new Result(Result.Outcome.INVALID_ARGS, String.format("Action %s does not exist for effect %s", effectName, actionName));
        if (cacheBehavior == null)
            return new Result(Result.Outcome.NOT_FOUND, String.format("No cache behavior was found for effect %s. Is the effect name correct?", effectName));

        EffectAction.ActionResult result = actions.get(actionName).apply(effect, params);

        if (cacheBehavior == CacheBehavior.PERSISTENT) {
            if (wasCached) {
                // this should never happen, but just in case
                effectCache.invalidate(id);
                activeEffects.put(id, effect);
            }

            return result;
        }

        switch (result.activeState) {
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

        return result;
    }

    public static void shutDown() {
        activeEffects.forEach((i, e) -> {
            Plugin.logger.info(String.format("Shutting down effect %s...", i));
            e.cleanup();
        });
        activeEffects.clear();
        effectCache.cleanUp();
        effectCache.asMap().forEach((i, e) -> {
            Plugin.logger.info(String.format("Shutting down effect %s...", i));
            e.cleanup();
        });
        effectCache.invalidateAll();
    }

    @Nullable
    private static Effect getInstance(String agentName) {
        Effect effect;
        try {
            effect = EffectRegistrar.getEffect(agentName);
        } catch (
            IllegalAccessException |
                InstantiationException |
                NoSuchMethodException |
                InvocationTargetException e
        ) {
            Plugin.logger.severe(e.getMessage());
            Plugin.logger.severe(Throwables.getStackTraceAsString(e));
            return null;
        }

        if (effect == null) {
            Plugin.logger.warning(String.format("Attempted to create an effect of type %s that doesn't exist", agentName));
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
