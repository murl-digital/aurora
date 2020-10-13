package fyi.sorenneedscoffee.aurora.managers;

import fyi.sorenneedscoffee.aurora.effects.EffectGroup;

public class BarManager {

  private static EffectGroup activeEffect;

  public static void showBar(EffectGroup group) throws Exception {
    group.startAll();
    activeEffect = group;
  }

  public static void clearBars() {
    activeEffect.stopAll(false);
    activeEffect = null;
  }

  public static boolean isActive() {
    return activeEffect != null;
  }
}
