package fyi.sorenneedscoffee.eyecandy.effects.particle;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.Effect;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class ParticleEffect extends Effect {
    private final Region region;
    private final Particle particle;
    private List<Location> particleLocations;
    private Runnable runnable;
    private BukkitTask task;
    private final Random random = new Random();

    public ParticleEffect(Region region, Particle particle) {
        this.region = region;
        this.particle = particle;
    }

    @Override
    public void init() {
        particleLocations = region.calculateLocations();
    }

    @Override
    public void execute(EffectAction action) {
        runnable = () -> {
            for (Location location : particleLocations) {
                if (region.type == RegionType.CUBOID || region.type == RegionType.EQUATION) {
                    if (region.randomized && action != EffectAction.TRIGGER) {
                        if (random.nextDouble() <= region.density) {
                            location.getWorld().spawnParticle(particle, location, 1);
                        }
                    } else {
                        if (random.nextDouble() <= 0.75) {
                            location.getWorld().spawnParticle(particle, location, 1);
                        }
                    }
                } else {
                    location.getWorld().spawnParticle(particle, location, 1);
                }
            }
        };
        if (action == EffectAction.TRIGGER) {
            Bukkit.getScheduler().runTask(EyeCandy.plugin, runnable);
        } else if (action == EffectAction.START) {
            task = Bukkit.getScheduler().runTaskTimer(EyeCandy.plugin, runnable, 0, 5);
        } else if (action == EffectAction.STOP) {
            task.cancel();
        }
    }

    @Override
    public void cleanup() {

    }
}
