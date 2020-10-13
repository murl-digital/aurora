package fyi.sorenneedscoffee.aurora.effects.time;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.points.Point;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

public class TimeShiftEffect extends Effect {

  private final World world;
  private final long amount;
  private BukkitTask task;

  public TimeShiftEffect(Point point, long amount) {
    world = point.getLocation().getWorld();
    this.amount = amount;
  }

  @Override
  public void init() {

  }

  @Override
  public void execute(EffectAction action) {
    if (action == EffectAction.START) {
      task = Bukkit.getScheduler()
          .runTaskTimer(Aurora.plugin, () -> world.setTime(world.getTime() + this.amount), 0, 1);
    } else if (action == EffectAction.STOP) {
      task.cancel();
    }
  }

  @Override
  public void cleanup() {

  }
}
