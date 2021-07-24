package digital.murl.aurora.effects.potion;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.Effect;
import digital.murl.aurora.effects.EffectAction;
import digital.murl.aurora.points.Point;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PotionEffect extends Effect {

  private final World world;
  private final org.bukkit.potion.PotionEffect potionEffect;
  private PotionListener listener;
  private boolean active = false;

  public PotionEffect(Point point, PotionEffectType type, int amplifier) {
    world = point.getLocation().getWorld();
    this.potionEffect = new org.bukkit.potion.PotionEffect(type, Integer.MAX_VALUE, amplifier,
        false, false, false);
  }

  @Override
  public void init() {
    listener = new PotionListener(Aurora.plugin, this);
    Aurora.protocolManager.addPacketListener(listener);
    getServer().getPluginManager().registerEvents(listener, Aurora.plugin);
  }

  @Override
  public void execute(EffectAction action) {
    if (action == EffectAction.START) {
      active = true;
      runTask(() -> {
        for (Player player : world.getPlayers()) {
          player.addPotionEffect(potionEffect);
        }
      });
    } else if (action == EffectAction.STOP) {
      active = false;
      runTask(() -> {
        for (Player player : world.getPlayers()) {
          player.removePotionEffect(potionEffect.getType());
        }
      });
    }
  }

  @Override
  public void cleanup() {
    Aurora.protocolManager.removePacketListener(listener);
    HandlerList.unregisterAll(listener);
    listener.clear();
  }

  protected void addEffect(Player player) {
    runTask(() -> player.addPotionEffect(potionEffect));
  }

  private static class PotionListener extends PacketAdapter implements Listener {

    private final List<UUID> watchList = new ArrayList<>();
    private final PotionEffect effect;

    public PotionListener(Plugin plugin, PotionEffect effect) {
      super(plugin,
          ListenerPriority.NORMAL,
          PacketType.Play.Client.POSITION
      );
      this.effect = effect;
    }

    protected void watch(UUID uuid) {
      watchList.add(uuid);
    }

    protected void clear() {
      watchList.clear();
    }

    @Override
    public void onPacketSending(PacketEvent event) {}

    @Override
    public void onPacketReceiving(PacketEvent event) {
      if (!watchList.isEmpty() && event.getPacketType() == PacketType.Play.Client.POSITION) {
        if (watchList.contains(event.getPlayer().getUniqueId())) {
          effect.addEffect(event.getPlayer());

          watchList.remove(event.getPlayer().getUniqueId());
        }
      }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
      if (effect.active) {
        watch(event.getPlayer().getUniqueId());
      }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
      if (effect.active) {
        event.getPlayer().removePotionEffect(effect.potionEffect.getType());
      }
    }
  }
}
