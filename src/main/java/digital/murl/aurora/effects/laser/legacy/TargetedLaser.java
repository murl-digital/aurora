package digital.murl.aurora.effects.laser.legacy;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * A whole class to create Guardian Beams by reflection </br> Inspired by the API <a
 * href="https://www.spigotmc.org/resources/guardianbeamapi.18329">GuardianBeamAPI</a></br>
 * <b>1.9 -> 1.15</b>
 *
 * @author SkytAsul
 * @see <a href="https://github.com/SkytAsul/GuardianBeam">GitHub page</a>
 */
class TargetedLaser {

  private final int duration;
  private final int distanceSquared;
  private final HashSet<Player> show = new HashSet<>();
  private Location start;
  private Object createGuardianPacket;
  private Object teamAddPacket;
  private Object destroyPacket;
  private Object metadataPacketGuardian;
  private Object fakeGuardianDataWatcher;
  private int guardian;
  private Player target;
  private UUID guardianUUID;
  private BukkitRunnable run;

  /**
   * Create a Laser instance
   *
   * @param start        Location where laser will starts
   * @param initalTarget Location where laser will ends
   * @param duration     Duration of laser in seconds (<i>-1 if infinite</i>)
   * @param distance     Distance where laser will be visible
   */
  public TargetedLaser(Location start, Player initalTarget, int duration, int distance)
      throws ReflectiveOperationException {
    this.start = start;
    this.duration = duration;
    distanceSquared = distance * distance;

    this.target = initalTarget;

    fakeGuardianDataWatcher = Packets.createFakeDataWatcher();
    createGuardianPacket = Packets
        .createPacketGuardianSpawn(start, fakeGuardianDataWatcher, target.getEntityId());
    guardian = (int) Packets.getField(Packets.packetSpawn, "a", createGuardianPacket);
    guardianUUID = (UUID) Packets.getField(Packets.packetSpawn, "b", createGuardianPacket);
    metadataPacketGuardian = Packets.createPacketMetadata(guardian, fakeGuardianDataWatcher);

    teamAddPacket = Packets.createPacketTeamAddEntities(guardianUUID);
    destroyPacket = Packets.createPacketRemoveEntities(guardian);
  }

  public void start(Plugin plugin) {
    Validate.isTrue(run == null, "Task already started");
    run = new BukkitRunnable() {
      int time = duration;

      @Override
      public void run() {
        try {
          if (time == 0) {
            cancel();
            return;
          }
          for (Player p : start.getWorld().getPlayers()) {
            if (isCloseEnough(p.getLocation())) {
              if (!show.contains(p)) {
                sendStartPackets(p);
                show.add(p);
              }
            } else if (show.contains(p)) {
              Packets.sendPacket(p, destroyPacket);
              show.remove(p);
            }
          }
          if (time != -1) {
            time--;
          }
        } catch (ReflectiveOperationException e) {
          e.printStackTrace();
        }
      }

      @Override
      public synchronized void cancel() throws IllegalStateException {
        super.cancel();
        try {
          for (Player p : show) {
            Packets.sendPacket(p, destroyPacket);
          }
        } catch (ReflectiveOperationException e) {
          e.printStackTrace();
        }
        run = null;
      }
    };
    run.runTaskTimerAsynchronously(plugin, 0L, 20L);
  }

  public void stop() {
    Validate.isTrue(run != null, "Task not started");
    run.cancel();
  }

  public void moveStart(Location location) throws ReflectiveOperationException {
    this.start = location;
    Object packet = Packets.createPacketMoveEntity(start, guardian);
    for (Player p : show) {
      Packets.sendPacket(p, packet);
    }
  }

  public Location getStart() {
    return start;
  }

  public void moveEnd(Player target) throws ReflectiveOperationException {
    for (Player p : show) {
      Packets.sendPacket(p, destroyPacket);
    }
    this.target = target;
    fakeGuardianDataWatcher = Packets.createFakeDataWatcher();
    createGuardianPacket = Packets
        .createPacketGuardianSpawn(start, fakeGuardianDataWatcher, target.getEntityId());
    guardian = (int) Packets.getField(Packets.packetSpawn, "a", createGuardianPacket);
    guardianUUID = (UUID) Packets.getField(Packets.packetSpawn, "b", createGuardianPacket);
    metadataPacketGuardian = Packets.createPacketMetadata(guardian, fakeGuardianDataWatcher);

    teamAddPacket = Packets.createPacketTeamAddEntities(guardianUUID);
    destroyPacket = Packets.createPacketRemoveEntities(guardian);

    for (Player p : show) {
      sendStartPackets(p);
    }
  }

  public Player getTarget() {
    return this.target;
  }

  public void callColorChange() throws ReflectiveOperationException {
    for (Player p : show) {
      Packets.sendPacket(p, metadataPacketGuardian);
    }
  }

  public boolean isStarted() {
    return run != null;
  }

  public void sendStartPackets(Player p) throws ReflectiveOperationException {
    Packets.sendPacket(p, createGuardianPacket);
    if (Packets.version > 14) {
      Packets.sendPacket(p, metadataPacketGuardian);
    }
    Packets.sendPacket(p, Packets.packetTeamCreate);
    Packets.sendPacket(p, teamAddPacket);
  }

  private boolean isCloseEnough(Location location) {
    return start.distanceSquared(location) <= distanceSquared ||
        target.getLocation().distanceSquared(location) <= distanceSquared;
  }


  private static class Packets {

    private static final int version = Integer.parseInt(
        Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]
            .substring(1).split("_")[1]);
    private static final String npack =
        "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName()
            .replace(".", ",").split(",")[3] + ".";
    private static final String cpack = Bukkit.getServer().getClass().getPackage().getName() + ".";
    private static int lastIssuedEID = 2000000000;
    private static Object packetTeamCreate;
    private static Constructor<?> watcherConstructor;
    private static Method watcherSet;
    private static Method watcherRegister;
    private static Method watcherDirty;
    private static Class<?> packetSpawn;
    private static Class<?> packetRemove;
    private static Class<?> packetTeleport;
    private static Class<?> packetTeam;
    private static Class<?> packetMetadata;
    private static Object watcherObject1; // invisilibity
    private static Object watcherObject2; // spikes
    private static Object watcherObject3; // attack id
    private static int guardianID;
    private static Object fakeSquid;
    private static Object fakeSquidWatcher;

    static {
      try {
        String watcherName1 = null, watcherName2 = null, watcherName3 = null;
        if (version < 13) {
          watcherName1 = "Z";
          watcherName2 = "bA";
          watcherName3 = "bB";
          guardianID = 68;
        } else if (version == 13) {
          watcherName1 = "ac";
          watcherName2 = "bF";
          watcherName3 = "bG";
          guardianID = 28;
        } else if (version == 14) {
          watcherName1 = "W";
          watcherName2 = "b";
          watcherName3 = "bD";
          guardianID = 30;
        } else if (version == 15) {
          watcherName1 = "T";
          watcherName2 = "b";
          watcherName3 = "bA";
          guardianID = 31;
        } else if (version >= 16) {
          watcherName1 = "T";
          watcherName2 = "b";
          watcherName3 = "d";
          guardianID = 31;
        }
        watcherObject1 = getField(Class.forName(npack + "Entity"), watcherName1, null);
        watcherObject2 = getField(Class.forName(npack + "EntityGuardian"), watcherName2, null);
        watcherObject3 = getField(Class.forName(npack + "EntityGuardian"), watcherName3, null);

        watcherConstructor = Class.forName(npack + "DataWatcher")
            .getDeclaredConstructor(Class.forName(npack + "Entity"));
        watcherSet = getMethod(Class.forName(npack + "DataWatcher"), "set");
        watcherRegister = getMethod(Class.forName(npack + "DataWatcher"), "register");
        if (version >= 15) {
          watcherDirty = getMethod(Class.forName(npack + "DataWatcher"), "markDirty");
        }
        packetSpawn = Class.forName(npack + "PacketPlayOutSpawnEntityLiving");
        packetRemove = Class.forName(npack + "PacketPlayOutEntityDestroy");
        packetTeleport = Class.forName(npack + "PacketPlayOutEntityTeleport");
        packetTeam = Class.forName(npack + "PacketPlayOutScoreboardTeam");
        packetMetadata = Class.forName(npack + "PacketPlayOutEntityMetadata");

        packetTeamCreate = packetTeam.getDeclaredConstructor().newInstance();
        setField(packetTeamCreate, "a", "noclip");
        setField(packetTeamCreate, "i", 0);
        setField(packetTeamCreate, "f", "never");

        Object world = Class.forName(cpack + "CraftWorld").getDeclaredMethod("getHandle")
            .invoke(Bukkit.getWorlds().get(0));
        Object[] entityConstructorParams = version < 14 ? new Object[]{world}
            : new Object[]{Class.forName(npack + "EntityTypes").getDeclaredField("SQUID").get(null),
                world};
        fakeSquid = getMethod(Class.forName(cpack + "entity.CraftSquid"), "getHandle").invoke(
            Class.forName(cpack + "entity.CraftSquid").getDeclaredConstructors()[0].newInstance(
                null, Class.forName(npack + "EntitySquid").getDeclaredConstructors()[0].newInstance(
                    entityConstructorParams)));
        fakeSquidWatcher = createFakeDataWatcher();
        tryWatcherSet(fakeSquidWatcher, watcherObject1, (byte) 32);
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
      }
    }

    static int generateEID() {
      return lastIssuedEID++;
    }

    public static void sendPacket(Player p, Object packet) throws ReflectiveOperationException {
      Object entityPlayer = Class.forName(cpack + "entity.CraftPlayer")
          .getDeclaredMethod("getHandle").invoke(p);
      Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection")
          .get(entityPlayer);
      playerConnection.getClass().getDeclaredMethod("sendPacket", Class.forName(npack + "Packet"))
          .invoke(playerConnection, packet);
    }

    public static Object createFakeDataWatcher() throws ReflectiveOperationException {
      Object watcher = watcherConstructor.newInstance(fakeSquid);
      if (version > 13) {
        setField(watcher, "registrationLocked", false);
      }
      return watcher;
    }

    public static Object createPacketGuardianSpawn(Location location, Object watcher, int squidId)
        throws ReflectiveOperationException {
      Object packet = packetSpawn.getDeclaredConstructor().newInstance();
      setField(packet, "a", generateEID());
      setField(packet, "b", UUID.randomUUID());
      setField(packet, "c", guardianID);
      setField(packet, "d", location.getX());
      setField(packet, "e", location.getY());
      setField(packet, "f", location.getZ());
      setField(packet, "j", (byte) (location.getYaw() * 256.0F / 360.0F));
      setField(packet, "k", (byte) (location.getPitch() * 256.0F / 360.0F));
      tryWatcherSet(watcher, watcherObject1, (byte) 32);
      tryWatcherSet(watcher, watcherObject2, false);
      tryWatcherSet(watcher, watcherObject3, squidId);
      if (version <= 14) {
        setField(packet, "m", watcher);
      }
      return packet;
    }

    public static Object createPacketRemoveEntities(int guardianId)
        throws ReflectiveOperationException {
      Object packet = packetRemove.getDeclaredConstructor().newInstance();
      setField(packet, "a", new int[]{guardianId});
      return packet;
    }

    public static Object createPacketMoveEntity(Location location, int entityId)
        throws ReflectiveOperationException {
      Object packet = packetTeleport.getDeclaredConstructor().newInstance();
      setField(packet, "a", entityId);
      setField(packet, "b", location.getX());
      setField(packet, "c", location.getY());
      setField(packet, "d", location.getZ());
      setField(packet, "e", (byte) (location.getYaw() * 256.0F / 360.0F));
      setField(packet, "f", (byte) (location.getPitch() * 256.0F / 360.0F));
      setField(packet, "g", true);
      return packet;
    }

    public static Object createPacketTeamAddEntities(UUID guardianUUID)
        throws ReflectiveOperationException {
      Object packet = packetTeam.getDeclaredConstructor().newInstance();
      setField(packet, "a", "noclip");
      setField(packet, "i", 3);
      Collection<String> players = (Collection<String>) getField(packetTeam, "h", packet);
      players.add(guardianUUID.toString());
      return packet;
    }

    private static Object createPacketMetadata(int entityId, Object watcher)
        throws ReflectiveOperationException {
      return packetMetadata.getConstructor(int.class, watcher.getClass(), boolean.class)
          .newInstance(entityId, watcher, false);
    }

    private static void tryWatcherSet(Object watcher, Object watcherObject, Object watcherData)
        throws ReflectiveOperationException {
      try {
        watcherSet.invoke(watcher, watcherObject, watcherData);
      } catch (InvocationTargetException ex) {
        watcherRegister.invoke(watcher, watcherObject, watcherData);
        if (version >= 15) {
          watcherDirty.invoke(watcher, watcherObject);
        }
      }
    }

    /* Reflection utils */
    private static Method getMethod(Class<?> clazz, String name) {
      for (Method m : clazz.getDeclaredMethods()) {
        if (m.getName().equals(name)) {
          return m;
        }
      }
      return null;
    }

    private static void setField(Object instance, String name, Object value)
        throws ReflectiveOperationException {
      Validate.notNull(instance);
      Field field = instance.getClass().getDeclaredField(name);
      field.setAccessible(true);
      field.set(instance, value);
    }

    private static Object getField(Class<?> clazz, String name, Object instance)
        throws ReflectiveOperationException {
      Field field = clazz.getDeclaredField(name);
      field.setAccessible(true);
      return field.get(instance);
    }
  }
}