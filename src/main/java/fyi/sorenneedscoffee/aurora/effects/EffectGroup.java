package fyi.sorenneedscoffee.aurora.effects;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EffectGroup {

  public final UUID id;
  public final CacheBehavior behavior;
  private final List<Effect> effects = new ArrayList<>();
  private final boolean isStatic;

  public EffectGroup(UUID id) {
    this.id = id;
    this.isStatic = false;
    this.behavior = CacheBehavior.DEFAULT;
  }

  public EffectGroup(UUID id, CacheBehavior behavior) {
    this.id = id;
    this.isStatic = false;
    this.behavior = behavior;
  }

  public EffectGroup(UUID id, boolean isStatic) {
    this.id = id;
    this.isStatic = isStatic;
    this.behavior = CacheBehavior.DEFAULT;
  }

  public void add(Effect effect) {
    effects.add(effect);
  }

  public void initAll() throws Exception {
    for (Effect e : effects) {
      e.init();
    }
  }

  public void startAll() {
    effects.forEach(e -> e.execute(EffectAction.START));
  }

  public void stopAll(boolean shuttingDown) {
    effects.forEach(e -> {
      if (!isStatic || shuttingDown) {
        e.execute(EffectAction.STOP);
        if (shuttingDown) {
          e.cleanup();
        }
      }
    });
  }

  public void cleanup() {
    effects.forEach(Effect::cleanup);
  }

  public void triggerAll() throws Exception {
    for (Effect e : effects) {
      e.init();
      e.execute(EffectAction.TRIGGER);
      e.cleanup();
    }
  }

  public void hotTriggerAll() {
    effects.forEach(e -> e.execute(EffectAction.TRIGGER));
  }

  public void restartAll() {
    effects.forEach(e -> e.execute(EffectAction.RESTART));
  }

  public <T> boolean instanceOf(Class<T> clazz) {
    for (Effect e : effects) {
        if (!clazz.isInstance(e)) {
            return false;
        }
    }

    return true;
  }

  public boolean isStatic() {
    return isStatic;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
    EffectGroup that = (EffectGroup) o;
    return Objects.equal(id, that.id);
  }
}
