package fyi.sorenneedscoffee.aurora.util;

import fyi.sorenneedscoffee.aurora.effects.EffectGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EffectManager {
    private static final List<EffectGroup> activeEffects = new ArrayList<>();

    public static void startEffect(EffectGroup group) throws Exception {
        group.startAll();
        activeEffects.add(group);
    }

    public static void stopEffect(UUID uuid) {
        EffectGroup effectGroup = findEffect(uuid);
        effectGroup.stopAll(false);
        activeEffects.remove(effectGroup);
    }

    public static void restartEffect(UUID uuid) {
        findEffect(uuid).restartAll();
    }

    public static void stopAll(boolean shuttingDown) {
        activeEffects.forEach(g -> g.stopAll(shuttingDown));
        activeEffects.clear();
    }

    public static void triggerEffect(EffectGroup group) throws Exception {
        group.triggerAll();
    }

    public static void hotTriggerEffect(UUID uuid) {
        findEffect(uuid).hotTriggerAll();
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
