package fyi.sorenneedscoffee.aurora.effects.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import org.bukkit.Location;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Squid;

public class GuardianLaserEffect extends Effect {
    @Override
    public void init() {

    }

    @Override
    public void execute(EffectAction action) {
        Location loc = Aurora.pointUtil.getPoint(0).getLocation();
        Guardian test = loc.getWorld().spawn(loc, Guardian.class);
        Squid stand = loc.getWorld().spawn(loc.clone().add(10, 10, 10), Squid.class);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        test.setSilent(true);
        test.setGravity(false);
        test.setTarget(stand);
    }

    @Override
    public void cleanup() {

    }
}
