package fyi.sorenneedscoffee.eyecandy.effects;

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

    public void startAll() {
        effects.forEach(e -> {
            e.init();
            e.execute(EffectAction.START);
        });
    }

    public void stopAll() {
        effects.forEach(e -> {
            e.execute(EffectAction.STOP);
            e.cleanup();
        });
    }

    public void triggerAll() {
        effects.forEach(e -> {
            e.init();
            e.execute(EffectAction.TRIGGER);
            e.cleanup();
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectGroup that = (EffectGroup) o;
        return Objects.equal(id, that.id);
    }
}
