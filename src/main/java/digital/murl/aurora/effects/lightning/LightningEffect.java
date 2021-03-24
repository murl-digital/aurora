package digital.murl.aurora.effects.lightning;

import com.comphenix.protocol.events.PacketContainer;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.Effect;
import digital.murl.aurora.effects.EffectAction;
import digital.murl.aurora.points.Point;
import digital.murl.aurora.wrapper.WrapperPlayServerSpawnEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LightningEffect extends Effect {

  private final Runnable runnable;
  private BukkitTask task;

  public LightningEffect(Point[] points, boolean spigotStrike) {
    if (spigotStrike) {
      runnable = () -> {
        for (Point p : points) {
          p.getLocation().getWorld().strikeLightningEffect(p.getLocation());
        }
      };
    } else {
      List<PacketContainer> packets = new ArrayList<>();

      int entityId = 6969;
      for (Point p : points) {
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity();
        packet.setEntityID(entityId++);
        packet.setUniqueId(UUID.randomUUID());
        packet.setType(EntityType.LIGHTNING);
        packet.setX(p.getLocation().getX());
        packet.setY(p.getLocation().getY());
        packet.setZ(p.getLocation().getZ());

        packets.add(packet.getHandle());
      }

      runnable = () -> {
        for (PacketContainer packet : packets) {
          Aurora.protocolManager.broadcastServerPacket(packet);
        }
      };
    }
  }

  @Override
  public void init() {

  }

  @Override
  public void execute(EffectAction action) {
    switch (action) {
      case START:
        task = Bukkit.getScheduler().runTaskTimer(Aurora.plugin, runnable, 0, 3);
        break;
      case STOP:
        task.cancel();
        break;
      case TRIGGER:
        Bukkit.getScheduler().runTask(Aurora.plugin, runnable);
    }
  }

  @Override
  public void cleanup() {

  }
}
