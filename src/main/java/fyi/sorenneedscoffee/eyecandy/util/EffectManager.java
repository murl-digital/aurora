package fyi.sorenneedscoffee.eyecandy.util;

import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EffectManager {
    private static final List<EffectGroup> activeEffects = new ArrayList<>();

    public static void startEffect(EffectGroup group) {
        group.startAll();
        activeEffects.add(group);
    }

    public static void stopEffect(UUID uuid) {
        EffectGroup effectGroup = findEffect(uuid);
        effectGroup.stopAll();
        activeEffects.remove(effectGroup);
    }

    public static void restartEffect(UUID uuid) {
        EffectGroup effectGroup = findEffect(uuid);
        effectGroup.restartAll();
    }

    public static void stopAll() {
        activeEffects.forEach(EffectGroup::stopAll);
        activeEffects.clear();
    }

    public static void triggerEffect(EffectGroup group) {
        group.triggerAll();
    }

    public static boolean exists(UUID id) {
        return findEffect(id) != null;
    }

    private static EffectGroup findEffect(UUID id) {
        Optional<EffectGroup> effect = activeEffects.stream().filter(e -> e.id.equals(id)).findAny();

        return effect.orElse(null);
    }
}
