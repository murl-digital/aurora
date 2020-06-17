package fyi.sorenneedscoffee.eyecandy.effects.potion;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.Effect;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class GlobalPotionEffect extends Effect {
    private final World world;
    private final PotionEffect potionEffect;
    private GlobalEffectListener listener;
    private boolean active = false;

    public GlobalPotionEffect(Point point, PotionEffectType type, int amplifier) {
        world = point.getLocation().getWorld();
        this.potionEffect = new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false, false);
    }

    @Override
    public void init() {
        listener = new GlobalPotionEffect.GlobalEffectListener(EyeCandy.plugin, this);
        EyeCandy.protocolManager.addPacketListener(listener);
        getServer().getPluginManager().registerEvents(listener, EyeCandy.plugin);
    }

    @Override
    public void execute(EffectAction action) {
        if(action == EffectAction.START) {
            active = true;
            Bukkit.getScheduler().runTask(EyeCandy.plugin, () -> {
                for(Player player : world.getPlayers()) {
                    player.addPotionEffect(potionEffect);
                }
            });
        } else if(action == EffectAction.STOP) {
            active = false;
            Bukkit.getScheduler().runTask(EyeCandy.plugin, () -> {
                for(Player player : world.getPlayers()) {
                    player.removePotionEffect(potionEffect.getType());
                }
            });
        }
    }

    @Override
    public void cleanup() {
        EyeCandy.protocolManager.removePacketListener(listener);
        HandlerList.unregisterAll(listener);
        listener.clear();
    }

    private static class GlobalEffectListener extends PacketAdapter implements Listener {
        private final List<UUID> watchList = new ArrayList<>();
        private final GlobalPotionEffect effect;

        public GlobalEffectListener(Plugin plugin, GlobalPotionEffect effect) {
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
        public void onPacketReceiving(PacketEvent event) {
            if (!watchList.isEmpty() && event.getPacketType() == PacketType.Play.Client.POSITION) {
                if (watchList.contains(event.getPlayer().getUniqueId())) {
                    event.getPlayer().addPotionEffect(effect.potionEffect);

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
            if(effect.active) {
                event.getPlayer().removePotionEffect(effect.potionEffect.getType());
            }
        }
    }
}
