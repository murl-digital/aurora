package fyi.sorenneedscoffee.aurora.effects.particle;

import com.comphenix.protocol.wrappers.WrappedParticle;
import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class ParticleEffect extends Effect {
    private final Region region;
    private final Particle particle;
    private final Random random = new Random();
    private List<Location> particleLocations;
    private Runnable runnable;
    private BukkitTask task;

    public ParticleEffect(Region region, Particle particle) {
        this.region = region;
        this.particle = particle;
    }

    @Override
    public void init() throws InterruptedException {
        particleLocations = region.calculateLocations();
    }

    @Override
    public void execute(EffectAction action) {
        runnable = () -> {
            WrapperPlayServerWorldParticles packet = new WrapperPlayServerWorldParticles();
            packet.setParticleType(WrappedParticle.create(particle, null));
            packet.setLongDistance(true);
            packet.setNumberOfParticles(1);
            for (Location location : particleLocations) {
                if (region.type == RegionType.CUBOID || region.type == RegionType.EQUATION) {
                    if (region.randomized && action != EffectAction.TRIGGER) {
                        if (random.nextDouble() <= region.density) {
                            packet.setX(location.getX());
                            packet.setY(location.getY());
                            packet.setZ(location.getZ());
                            Aurora.protocolManager.broadcastServerPacket(packet.getHandle());
                        }
                    } else {
                        if (random.nextDouble() <= 0.75) {
                            packet.setX(location.getX());
                            packet.setY(location.getY());
                            packet.setZ(location.getZ());
                            Aurora.protocolManager.broadcastServerPacket(packet.getHandle());
                        }
                    }
                } else {
                    packet.setX(location.getX());
                    packet.setY(location.getY());
                    packet.setZ(location.getZ());
                    Aurora.protocolManager.broadcastServerPacket(packet.getHandle());
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
