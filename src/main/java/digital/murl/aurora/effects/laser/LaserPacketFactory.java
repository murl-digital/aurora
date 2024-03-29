package digital.murl.aurora.effects.laser;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import digital.murl.aurora.wrapper.WrapperPlayServerEntityDestroy;
import digital.murl.aurora.wrapper.WrapperPlayServerEntityMetadata;
import digital.murl.aurora.wrapper.WrapperPlayServerSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.EntityGuardian;
import net.minecraft.server.v1_16_R3.EntitySquid;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftGuardian;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftSquid;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class LaserPacketFactory {

  public static final Entity fakeSquid;
  private static int lastIssuedEID = 2000000000;

  static {
    fakeSquid = new CraftSquid((CraftServer) Bukkit.getServer(), new EntitySquid(
        EntityTypes.SQUID,
        ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle()
    ));
  }

  public static WrapperPlayServerSpawnEntityLiving createSquidPacket(Location loc) {
    WrapperPlayServerSpawnEntityLiving spawnSquidPacket = new WrapperPlayServerSpawnEntityLiving();
    spawnSquidPacket.setEntityID(generateEID());
    spawnSquidPacket.setUniqueId(UUID.randomUUID());
    spawnSquidPacket.setType(80);
    spawnSquidPacket.setX(loc.getX());
    spawnSquidPacket.setY(loc.getY());
    spawnSquidPacket.setZ(loc.getZ());
    spawnSquidPacket.setYaw(0.0f);
    spawnSquidPacket.setPitch(0.0f);

    return spawnSquidPacket;
  }

  public static WrapperPlayServerSpawnEntityLiving createGuardianPacket(Location loc) {
    WrapperPlayServerSpawnEntityLiving spawnGuardianPacket = new WrapperPlayServerSpawnEntityLiving();
    spawnGuardianPacket.setEntityID(generateEID());
    spawnGuardianPacket.setUniqueId(UUID.randomUUID());
    spawnGuardianPacket.setType(31);
    spawnGuardianPacket.setX(loc.getX());
    spawnGuardianPacket.setY(loc.getY());
    spawnGuardianPacket.setZ(loc.getZ());
    spawnGuardianPacket.setYaw(0.0f);
    spawnGuardianPacket.setPitch(0.0f);

    return spawnGuardianPacket;
  }

  public static WrapperPlayServerEntityDestroy createDestroyPacket(int[] ids) {
    WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
    destroyPacket.setEntityIds(ids);

    return destroyPacket;
  }

  public static WrapperPlayServerEntityMetadata createInvisibilityPacket(int eId,
                                                                         Entity fakeEntity) {
    WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(fakeEntity);
    watcher.setObject(0, (byte) 32);

    WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
    metadataPacket.setEntityID(eId);
    metadataPacket.setMetadata(watcher.getWatchableObjects());

    return metadataPacket;
  }

  public static WrapperPlayServerEntityMetadata createGuardianMetaPacket(int eId, int targetEId) {
    WrappedDataWatcher guardianWatcher = WrappedDataWatcher.getEntityWatcher(fakeGuardian());
    guardianWatcher.setObject(0, (byte) 32);
    guardianWatcher.setObject(15, false);
    guardianWatcher.setObject(16, targetEId);

    WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
    metadataPacket.setEntityID(eId);
    metadataPacket.setMetadata(guardianWatcher.getWatchableObjects());

    return metadataPacket;
  }

  private static int generateEID() {
    return lastIssuedEID++;
  }

  private static Entity fakeGuardian() {
    return new CraftGuardian((CraftServer) Bukkit.getServer(), new EntityGuardian(
        EntityTypes.GUARDIAN,
        ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle()
    ));
  }
}
