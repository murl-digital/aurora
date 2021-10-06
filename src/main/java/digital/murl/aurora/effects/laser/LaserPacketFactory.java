package digital.murl.aurora.effects.laser;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.wrapper.WrapperPlayServerEntityMetadata;
import digital.murl.aurora.wrapper.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class LaserPacketFactory {
  private static int lastIssuedEID = 2000000000;

  private static final String npack =
          "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName()
                  .replace(".", ",").split(",")[3] + ".";
  private static final String cpack = Bukkit.getServer().getClass().getPackage().getName() + ".";
  private static final int version = Integer.parseInt(
          Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]
                  .substring(1).split("_")[1]);

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
    spawnGuardianPacket.setType(version > 16 ? 35 : 31);
    spawnGuardianPacket.setX(loc.getX());
    spawnGuardianPacket.setY(loc.getY());
    spawnGuardianPacket.setZ(loc.getZ());
    spawnGuardianPacket.setYaw(0.0f);
    spawnGuardianPacket.setPitch(0.0f);

    return spawnGuardianPacket;
  }


  public static PacketContainer createDestroyPacket(int[] ids) {
    PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
    if (version > 16) packetContainer.getIntLists().write(0, Arrays.stream(ids).boxed().collect(Collectors.toList()));
    else packetContainer.getIntegerArrays().write(0, ids);

    return packetContainer;
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
    guardianWatcher.setObject(version > 16 ? 16 : 15, false);
    guardianWatcher.setObject(version > 16 ? 17 : 16, targetEId);

    WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
    metadataPacket.setEntityID(eId);
    metadataPacket.setMetadata(guardianWatcher.getWatchableObjects());

    return metadataPacket;
  }

  private static int generateEID() {
    return lastIssuedEID++;
  }

  private static Entity fakeGuardian() {
    try {
      Object world = Class.forName(cpack + "CraftWorld").getDeclaredMethod("getHandle")
              .invoke(Bukkit.getWorlds().get(0));


      Object[] entityConstructorParams = version < 14
              ? new Object[]{world}
              : version > 16
                ? new Object[]{Class.forName("net.minecraft.world.entity.EntityTypes").getDeclaredField("K").get(null), world}
                : new Object[]{Class.forName(npack + "EntityTypes").getDeclaredField("GUARDIAN").get(null), world};


      Object entityGuardian = version <= 16
              ? Class.forName(npack + "EntityGuardian").getDeclaredConstructors()[0].newInstance(entityConstructorParams)
              : Class.forName("net.minecraft.world.entity.monster.EntityGuardian").getDeclaredConstructors()[0]
                .newInstance(entityConstructorParams);
      return (Entity) Class.forName(cpack + "entity.CraftGuardian").getDeclaredConstructors()[0].newInstance(
              null, entityGuardian);
    } catch (Exception e) {
      Aurora.logger.severe(e.getMessage());
      return null;
    }

    /*return new CraftGuardian((CraftServer) Bukkit.getServer(), new EntityGuardian(
        EntityTypes.GUARDIAN,
        ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle()
    ));*/
  }
}
