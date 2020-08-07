package fyi.sorenneedscoffee.aurora.effects.laser;

import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;

public class EndLaserEffect extends Effect {
    private final Location start;
    private final Location end;
    private EnderCrystal crystal;

    public EndLaserEffect(Location start, Location end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void init() {

    }

    @Override
    public void execute(EffectAction action) {
        switch (action) {
            case START:
                runTask(() -> {
                    crystal = start.getWorld().spawn(start, EnderCrystal.class);
                    crystal.setShowingBottom(false);
                    crystal.setGravity(false);
                    crystal.setBeamTarget(end);
                });
                break;
            case STOP:
                runTask(() -> crystal.remove());
        }
    }

    @Override
    public void cleanup() {

    }
}
