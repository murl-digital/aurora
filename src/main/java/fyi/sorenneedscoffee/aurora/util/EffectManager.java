package fyi.sorenneedscoffee.aurora.util;

import fyi.sorenneedscoffee.aurora.effects.EffectGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EffectManager {
    private static final List<EffectGroup> activeEffects = new ArrayList<>();
    private static final ExecutorService service = Executors.newSingleThreadExecutor();

    public static void startEffect(EffectGroup group) throws Exception {
        try {
            service.submit((Callable<Void>) () -> {
                group.startAll();
                activeEffects.add(group);
                return null;
            }).get();
        } catch (ExecutionException e) {
            throw (Exception) e.getCause();
        }
    }

    public static void stopEffect(UUID uuid) {
        service.submit(() -> {
            EffectGroup effectGroup = findEffect(uuid);
            if (effectGroup != null) {
                effectGroup.stopAll(false);
                activeEffects.remove(effectGroup);
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
        if (shuttingDown)
            service.shutdownNow();

        activeEffects.forEach(g -> g.stopAll(shuttingDown));
        activeEffects.clear();
    }

    public static void triggerEffect(EffectGroup group) throws Exception {
        group.triggerAll();
    }

    public static void hotTriggerEffect(UUID uuid) {
        service.submit(() -> {
            EffectGroup effectGroup = findEffect(uuid);
            if(effectGroup != null) {
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
}
