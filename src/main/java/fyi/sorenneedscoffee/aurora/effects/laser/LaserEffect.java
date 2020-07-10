package fyi.sorenneedscoffee.aurora.effects.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.bukkit.Bukkit;

public class LaserEffect extends Effect {
    private final Point start;
    private final Point end;
    private ReferenceLaser laser;

    public LaserEffect(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void init() throws ReflectiveOperationException {
        laser = new ReferenceLaser(
                start.getLocation(),
                end.getLocation(),
                -1,
                256
        );
    }

    @Override
    public void execute(EffectAction action) {
        if (action == EffectAction.START) {
            runTask(() -> laser.start(Aurora.plugin));
        } else if (action == EffectAction.STOP) {
            runTask(() -> laser.stop());
        }
    }

    @Override
    public void cleanup() {

    }
}
