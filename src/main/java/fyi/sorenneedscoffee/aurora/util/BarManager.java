package fyi.sorenneedscoffee.aurora.util;

import fyi.sorenneedscoffee.aurora.effects.EffectGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BarManager {
    private static EffectGroup activeEffect;

    public static void showBar(EffectGroup group) throws Exception {
        group.startAll();
        activeEffect = group;
    }

    public static void clearBars() {
        activeEffect.stopAll();
        activeEffect = null;
    }

    public static boolean isActive() {
        return activeEffect != null;
    }
}
