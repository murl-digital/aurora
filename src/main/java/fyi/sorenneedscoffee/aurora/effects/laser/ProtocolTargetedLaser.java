package fyi.sorenneedscoffee.aurora.effects.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerEntityDestroy;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerEntityMetadata;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtocolTargetedLaser {
    private final BukkitRunnable runnable;
    private boolean isActive = false;

    private final WrapperPlayServerSpawnEntityLiving spawnGuardianPacket;
    private WrapperPlayServerEntityMetadata guardianMetadataPacket;
    //dont ask.
    private final WrapperPlayServerEntityDestroy theEndIsNigh;


    public ProtocolTargetedLaser(Location start, Player target) {

        this.spawnGuardianPacket = LaserPacketFactory.createGuardianPacket(start);
        this.guardianMetadataPacket = LaserPacketFactory.createGuardianMetaPacket(spawnGuardianPacket.getEntityID(), target.getEntityId());
        this.theEndIsNigh = LaserPacketFactory.createDestroyPacket(new int[]{spawnGuardianPacket.getEntityID()});

        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Aurora.protocolManager.broadcastServerPacket(spawnGuardianPacket.getHandle());
                Aurora.protocolManager.broadcastServerPacket(guardianMetadataPacket.getHandle());
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                Aurora.protocolManager.broadcastServerPacket(theEndIsNigh.getHandle());
            }
        };
    }

    public void start() {
        this.isActive = true;
        runnable.runTaskAsynchronously(Aurora.plugin);
    }

    public void stop() {
        this.isActive = false;
        runnable.cancel();
    }

    public void changeTarget(Player target) {
        guardianMetadataPacket = LaserPacketFactory.createGuardianMetaPacket(spawnGuardianPacket.getEntityID(), target.getEntityId());
        new BukkitRunnable() {
            @Override
            public void run() {
                Aurora.protocolManager.broadcastServerPacket(guardianMetadataPacket.getHandle());
            }
        }.runTaskAsynchronously(Aurora.plugin);
    }

    public boolean isStarted() {
        return isActive;
    }

    public void sendStartPackets(Player p) {
        if (!p.isOnline()) return;

        try {
            Aurora.protocolManager.sendServerPacket(p, spawnGuardianPacket.getHandle());
            Aurora.protocolManager.sendServerPacket(p, guardianMetadataPacket.getHandle());
        } catch (Exception ignore) {}
    }

    public void callColorChange() {
        Aurora.protocolManager.broadcastServerPacket(guardianMetadataPacket.getHandle());
    }
}
