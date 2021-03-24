package digital.murl.aurora.effects.laser;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.Effect;
import digital.murl.aurora.effects.EffectAction;
import digital.murl.aurora.points.Point;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class LaserEffect extends Effect {

  private final Point start, end;
  protected ProtocolLaser laser;
  private ArmorStand marker;
  private LaserEffect.LaserListener laserListener;

  public LaserEffect(Point start, Point end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public void init() throws ReflectiveOperationException {
    runTask(() -> {
      marker = end.getLocation().getWorld().spawn(end.getLocation(), ArmorStand.class);
      marker.setVisible(false);
      marker.setMarker(true);
      marker.setGravity(false);

      laser = new ProtocolLaser(
          start.getLocation(),
          marker
      );
    });
    laserListener = new LaserListener(Aurora.plugin, this);
    Aurora.protocolManager.addPacketListener(laserListener);
    Bukkit.getPluginManager().registerEvents(laserListener, Aurora.plugin);
  }

  @Override
  public void execute(EffectAction action) {
    switch (action) {
      case START:
        runTask(() -> laser.start());
        break;
      case TRIGGER:
        runTask(() -> {
          try {
            laser.callColorChange();
          } catch (Exception e) {
            Aurora.logger.warning("An error occurred while attempting to change a laser's color.");
            Aurora.logger.warning(e.getMessage());
            Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
          }
        });
        break;
      case STOP:
        runTask(() -> laser.stop());
        break;
    }
  }

  @Override
  public void cleanup() {
    runTask(() -> marker.remove());
    Aurora.protocolManager.removePacketListener(laserListener);
    HandlerList.unregisterAll(laserListener);
  }

  private static class LaserListener extends PacketAdapter implements Listener {

    private final List<Player> activePlayers;
    private final LaserEffect effect;

    protected LaserListener(Plugin plugin, LaserEffect effect) {
      super(plugin,
          ListenerPriority.NORMAL,
          PacketType.Play.Client.POSITION
      );
      this.effect = effect;
      activePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
      if (effect.laser.isStarted() && !activePlayers.contains(event.getPlayer())) {
        try {
          activePlayers.add(event.getPlayer());
          effect.laser.sendStartPackets(event.getPlayer());
        } catch (Exception ignored) {
        }
      }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
      activePlayers.remove(event.getPlayer());
    }
  }
}
