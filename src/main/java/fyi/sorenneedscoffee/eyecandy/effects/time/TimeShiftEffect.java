package fyi.sorenneedscoffee.eyecandy.effects.time;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.Effect;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class TimeShiftEffect extends Effect {
    private final World world;
    private final long amount;
    private final long period;
    private BukkitTask task;

    public TimeShiftEffect(Point point, long amount, long period) {
        world = point.getLocation().getWorld();
        this.amount = amount;
        this.period = period;
    }

    @Override
    public void init() {

    }

    @Override
    public void execute(EffectAction action) {
        if (action == EffectAction.START) {
            task = Bukkit.getScheduler().runTaskTimer(EyeCandy.plugin, () -> world.setTime(world.getTime() + this.amount), 0, period);
        } else if (action == EffectAction.STOP) {
            task.cancel();
        }
    }

    @Override
    public void cleanup() {

    }
}
