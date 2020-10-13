package fyi.sorenneedscoffee.aurora.effects.particle;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

public class ParticleEffect extends Effect {

  private final Region region;
  private final Particle particle;
  private final Object options;
  private final Random random = new Random();
  private Set<Location> particleLocations;
  private Runnable runnable;
  private BukkitTask task;

  public ParticleEffect(Region region, Particle particle, Object options) {
    this.region = region;
    this.particle = particle;
    this.options = options;
  }

  @Override
  public void init() throws InterruptedException {
    particleLocations = region.calculateLocations();
  }

  @Override
  public void execute(EffectAction action) {
    runnable = () -> {
      for (Location location : particleLocations) {
        if (region.type == RegionType.CUBOID || region.type == RegionType.EQUATION) {
          if (region.randomized && action != EffectAction.TRIGGER) {
            if (random.nextDouble() <= region.density) {
              location.getWorld().spawnParticle(
                  particle,
                  location,
                  1,
                  0.5,
                  0.5,
                  0.5,
                  0.1,
                  options,
                  true
              );
            }
          } else {
            if (random.nextDouble() <= 0.75) {
              location.getWorld().spawnParticle(
                  particle,
                  location,
                  1,
                  0.5,
                  0.5,
                  0.5,
                  0.1,
                  options,
                  true
              );
            }
          }
        } else {
          location.getWorld().spawnParticle(
              particle,
              location,
              1,
              0.5,
              0.5,
              0.5,
              0.1,
              options,
              true
          );
        }
      }
    };
    if (action == EffectAction.TRIGGER) {
      runTask(runnable);
    } else if (action == EffectAction.START) {
      task = Bukkit.getScheduler().runTaskTimer(Aurora.plugin, runnable, 0, 5);
    } else if (action == EffectAction.STOP) {
      task.cancel();
    }
  }

  @Override
  public void cleanup() {
  }
}
