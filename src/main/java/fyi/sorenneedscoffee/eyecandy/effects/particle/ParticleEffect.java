package fyi.sorenneedscoffee.eyecandy.effects.particle;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.Effect;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;
import fyi.sorenneedscoffee.eyecandy.http.requests.RegionType;
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
    private BukkitTask task;

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
        if (action == EffectAction.TRIGGER) {
            Bukkit.getScheduler().runTask(EyeCandy.plugin, () -> {
                for (Location location : particleLocations) {
                    location.getWorld().spawnParticle(particle, location, 1);
                }
            });
        } else if (action == EffectAction.START) {
            Random random = new Random();
            task = Bukkit.getScheduler().runTaskTimer(EyeCandy.plugin, () -> {
                for (Location location : particleLocations) {
                    if(region.type == RegionType.CUBOID || region.type == RegionType.EQUATION) {
                        if(region.randomized) {
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
            }, 0, 5);
        } else if (action == EffectAction.STOP) {
            task.cancel();
        }
    }

    @Override
    public void cleanup() {

    }
}
