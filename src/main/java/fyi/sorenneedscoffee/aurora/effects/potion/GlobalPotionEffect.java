package fyi.sorenneedscoffee.aurora.effects.potion;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.util.Point;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.Bukkit.getServer;

public class GlobalPotionEffect extends Effect {
    private final World world;
    private final PotionEffect potionEffect;
    private BukkitTask task;

    public GlobalPotionEffect(Point point, PotionEffectType type, int amplifier) {
        world = point.getLocation().getWorld();
        this.potionEffect = new PotionEffect(type, 225, amplifier, false, false, false);
    }

    @Override
    public void init() {
    }

    @Override
    public void execute(EffectAction action) {
        if (action == EffectAction.START) {
            task = getScheduler().runTaskTimer(Aurora.plugin, () -> {
                for (Player player : world.getPlayers()) {
                    player.removePotionEffect(potionEffect.getType());
                    player.addPotionEffect(potionEffect);
                }
            }, 0, 20);
        } else if (action == EffectAction.STOP) {
            task.cancel();
        }
    }

    @Override
    public void cleanup() {
        runTask(() -> {
            for (Player player : world.getPlayers()) {
                player.removePotionEffect(potionEffect.getType());
            }
        });
    }
}
