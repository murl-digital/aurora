package fyi.sorenneedscoffee.aurora.util;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EffectManager {
    private static final List<EffectGroup> activeEffects = new ArrayList<>();
    private static final List<EffectGroup> cachedEffects = new ArrayList<>();
    private static final Timer timer = new Timer();
    private static final long CLEAR_INTERVAL = 5000;
    private static final ExecutorService service = Executors.newSingleThreadExecutor();

    static {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (EffectGroup e : cachedEffects) {
                    Aurora.logger.info("Clearing " + e.getClass().getSimpleName());
                    e.cleanup();
                }
                cachedEffects.clear();
            }
        }, 0, CLEAR_INTERVAL);
    }

    public static void startEffect(EffectGroup group) throws Throwable {
        try {
            service.submit(() -> {
                EffectGroup cachedEffect = findCachedEffect(group.id);
                if (cachedEffect != null) {
                    cachedEffects.remove(cachedEffect);
                    cachedEffect.startAll();
                    activeEffects.add(cachedEffect);
                } else {
                    group.initAll();
                    group.startAll();
                    activeEffects.add(group);
                }
                return null;
            }).get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    public static void stopEffect(UUID uuid) {
        service.submit(() -> {
            EffectGroup effectGroup = findEffect(uuid);
            if (effectGroup != null) {
                effectGroup.stopAll(false);
                activeEffects.remove(effectGroup);
                cachedEffects.add(effectGroup);
            }
        });
    }

    public static void restartEffect(UUID uuid) {
        service.submit(() -> {
            EffectGroup effectGroup = findEffect(uuid);
            if (effectGroup != null) {
                effectGroup.restartAll();
            }
        });
    }

    public static void stopAll(boolean shuttingDown) {
        if (shuttingDown) {
            timer.cancel();
            service.shutdownNow();
        }

        activeEffects.forEach(g -> g.stopAll(shuttingDown));
        activeEffects.clear();
    }

    public static void triggerEffect(EffectGroup group) throws Exception {
        group.triggerAll();
    }

    public static void hotTriggerEffect(UUID uuid) {
        service.submit(() -> {
            EffectGroup effectGroup = findEffect(uuid);
            if (effectGroup != null) {
                effectGroup.hotTriggerAll();
            }
        });
    }

    public static boolean exists(UUID id) {
        return findEffect(id) != null;
    }

    public static <T> boolean instanceOf(UUID id, Class<T> clazz) {
        EffectGroup group = findEffect(id);
        if (group == null)
            return false;

        return group.instanceOf(clazz);
    }

    private static EffectGroup findEffect(UUID id) {
        return activeEffects.contains(new EffectGroup(id)) ? activeEffects.get(activeEffects.indexOf(new EffectGroup(id))) : null;
    }

    private static EffectGroup findCachedEffect(UUID id) {
        return cachedEffects.contains(new EffectGroup(id)) ? cachedEffects.get(cachedEffects.indexOf(new EffectGroup(id))) : null;
    }
}
