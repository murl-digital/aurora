package fyi.sorenneedscoffee.aurora.managers;

import com.google.common.cache.*;
import fyi.sorenneedscoffee.aurora.effects.CacheBehavior;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import io.netty.util.internal.ConcurrentSet;

import java.util.*;
import java.util.concurrent.*;

public class EffectManager {
    private static final Map<UUID ,EffectGroup> activeEffects = new ConcurrentHashMap<>();
    private static final Set<EffectGroup> staticEffects = new ConcurrentSet<>();

    private static final RemovalListener<UUID, EffectGroup> listener = r -> r.getValue().cleanup();
    private static final Cache<UUID, EffectGroup> cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .removalListener(listener)
            .build();
    private static final Cache<UUID, EffectGroup> persistentCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .removalListener(listener)
            .build();

    public static void startEffect(EffectGroup group) throws Throwable {
        if (group.isStatic()) {
            group.initAll();
            group.startAll();
            staticEffects.add(group);
        }

        try {
            EffectGroup cachedEffect = getCachedEffect(group.id);
            if (cachedEffect != null) {
                cachedEffect.startAll();
                activeEffects.put(cachedEffect.id, cachedEffect);
            } else {
                group.initAll();
                group.startAll();
                activeEffects.put(group.id, group);
            }
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    public static void stopEffect(UUID uuid) {
        EffectGroup effectGroup = activeEffects.get(uuid);
        if (effectGroup != null) {
            effectGroup.stopAll(false);
            activeEffects.remove(uuid);
            cache(effectGroup);
        }
    }

    public static void restartEffect(UUID uuid) {
        EffectGroup effectGroup = activeEffects.get(uuid);
        if (effectGroup != null) {
            effectGroup.restartAll();
        }
    }

    public static void stopAll(boolean shuttingDown) {
        if (shuttingDown) {
            persistentCache.invalidateAll();
            cache.invalidateAll();
            cache.cleanUp();
            persistentCache.cleanUp();

            staticEffects.forEach(g -> g.stopAll(true));
            staticEffects.clear();
        }

        activeEffects.forEach((u, g) -> {
            g.stopAll(shuttingDown);
            if (!shuttingDown) cache(g);
        });
        activeEffects.clear();
    }

    public static void triggerEffect(EffectGroup group) throws Exception {
        group.triggerAll();
    }

    public static void hotTriggerEffect(UUID uuid) {
            EffectGroup effectGroup = activeEffects.get(uuid);
            if (effectGroup != null) {
                effectGroup.hotTriggerAll();
            }
    }

    public static boolean exists(UUID id) {
        return activeEffects.containsKey(id);
    }

    public static void cache(EffectGroup group) {
        if (group.behavior == CacheBehavior.DISABLED)
            return;

        if (!isCached(group.id)) {
            if (group.behavior == CacheBehavior.PERSIST)
                persistentCache.put(group.id, group);
            else
                cache.put(group.id, group);
        }
    }

    public static boolean isCached(UUID id) {
        return persistentCache.asMap().containsKey(id) || persistentCache.asMap().containsKey(id);
    }

    public static EffectGroup getCachedEffect(UUID id) {
        EffectGroup result;
        result = persistentCache.getIfPresent(id);
        if (result != null) return result;
        result = cache.getIfPresent(id);
        return result;
    }

    public static <T> boolean instanceOf(UUID id, Class<T> clazz) {
        return activeEffects.containsKey(id) && activeEffects.get(id).instanceOf(clazz);
    }
}
