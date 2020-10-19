package fyi.sorenneedscoffee.aurora.effects.lightning;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.points.Point;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class LightningEffect extends Effect {

  private final Runnable runnable;
  private BukkitTask task;

  public LightningEffect(Point[] points) {
    runnable = () -> {
      for (Point p : points) {
        p.getLocation().getWorld().strikeLightningEffect(p.getLocation());
      }
    };
  }

  @Override
  public void init() {

  }

  @Override
  public void execute(EffectAction action) {
    switch (action) {
      case START:
        task = Bukkit.getScheduler().runTaskTimer(Aurora.plugin, runnable, 0, 1);
        break;
      case STOP:
        task.cancel();
    }
  }

  @Override
  public void cleanup() {

  }
}
