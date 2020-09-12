package fyi.sorenneedscoffee.aurora.effects.laser;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerEntityDestroy;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerEntityMetadata;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerSpawnEntityLiving;
import net.minecraft.server.v1_16_R2.EntityGuardian;
import net.minecraft.server.v1_16_R2.EntitySquid;
import net.minecraft.server.v1_16_R2.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftGuardian;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftSquid;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ProtocolLaser {
    private final Location start, end;
    private final BukkitRunnable runnable;
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

        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                Aurora.protocolManager.broadcastServerPacket(spawnSquidPacket.getHandle());
                Aurora.protocolManager.broadcastServerPacket(spawnGuardianPacket.getHandle());
                Aurora.protocolManager.broadcastServerPacket(squidMetadataPacket.getHandle());
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
