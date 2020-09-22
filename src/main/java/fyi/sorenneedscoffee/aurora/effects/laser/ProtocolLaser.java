package fyi.sorenneedscoffee.aurora.effects.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerEntityDestroy;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerEntityMetadata;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ProtocolLaser {
    private final Location start, end;
    private boolean isActive = false;

    private final WrapperPlayServerSpawnEntityLiving spawnSquidPacket, spawnGuardianPacket;
    private final WrapperPlayServerEntityMetadata squidMetadataPacket, guardianMetadataPacket;
    //dont ask.
    private final WrapperPlayServerEntityDestroy theEndIsNigh;


    public ProtocolLaser(Location start, Location end) {
        this.start = start;
        this.end = end;

        this.spawnSquidPacket = LaserPacketFactory.createSquidPacket(end);
        this.squidMetadataPacket = LaserPacketFactory.createInvisibilityPacket(spawnSquidPacket.getEntityID(), LaserPacketFactory.fakeSquid);
        this.spawnGuardianPacket = LaserPacketFactory.createGuardianPacket(start);
        this.guardianMetadataPacket = LaserPacketFactory.createGuardianMetaPacket(spawnGuardianPacket.getEntityID(), spawnSquidPacket.getEntityID());
        this.theEndIsNigh = LaserPacketFactory.createDestroyPacket(new int[]{spawnSquidPacket.getEntityID(), spawnGuardianPacket.getEntityID()});
    }

    public void start() {
        this.isActive = true;
        Aurora.protocolManager.broadcastServerPacket(spawnSquidPacket.getHandle());
        Aurora.protocolManager.broadcastServerPacket(spawnGuardianPacket.getHandle());
        Aurora.protocolManager.broadcastServerPacket(squidMetadataPacket.getHandle());
        Aurora.protocolManager.broadcastServerPacket(guardianMetadataPacket.getHandle());
    }

    public void stop() {
        this.isActive = false;
        Aurora.protocolManager.broadcastServerPacket(theEndIsNigh.getHandle());
    }

    public boolean isStarted() {
        return isActive;
    }

    public void sendStartPackets(Player p) {
        if (!p.isOnline()) return;

        try {
            Aurora.protocolManager.sendServerPacket(p, spawnSquidPacket.getHandle());
            Aurora.protocolManager.sendServerPacket(p, spawnGuardianPacket.getHandle());
            Aurora.protocolManager.sendServerPacket(p, squidMetadataPacket.getHandle());
            Aurora.protocolManager.sendServerPacket(p, guardianMetadataPacket.getHandle());
        } catch (Exception ignore) {}
    }

    public void callColorChange() {
        Aurora.protocolManager.broadcastServerPacket(guardianMetadataPacket.getHandle());
    }
}
