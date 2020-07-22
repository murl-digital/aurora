package fyi.sorenneedscoffee.aurora.effects;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EffectGroup {
    public final UUID id;
    private final List<Effect> effects = new ArrayList<>();

    public EffectGroup(UUID id) {
        this.id = id;
    }

    public void add(Effect effect) {
        effects.add(effect);
    }

    public void startAll() throws Exception {
        for (Effect e : effects) {
            e.init();
            e.execute(EffectAction.START);
        }
    }

    public void stopAll() {
        effects.forEach(e -> {
            e.execute(EffectAction.STOP);
            e.cleanup();
        });
    }

    public void triggerAll() throws Exception {
        for (Effect e : effects) {
            e.init();
            e.execute(EffectAction.TRIGGER);
            e.cleanup();
        }
    }

    public void restartAll() {
        effects.forEach(e -> e.execute(EffectAction.RESTART));
    }

    public <T> boolean instanceOf(Class<T> clazz) {
        for (Effect e : effects) {
            if (!e.getClass().isInstance(clazz))
                return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectGroup that = (EffectGroup) o;
        return Objects.equal(id, that.id);
    }
}
