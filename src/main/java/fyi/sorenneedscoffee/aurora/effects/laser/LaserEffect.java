package fyi.sorenneedscoffee.aurora.effects.laser;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class LaserEffect extends Effect {
    private final Point start;
    private final Point end;
    protected Laser laser;
    private LaserEffect.LaserListener laserListener;

    public LaserEffect(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void init() throws ReflectiveOperationException {
        laser = new Laser(
                start.getLocation(),
                end.getLocation(),
                -1,
                256
        );
        laserListener = new LaserListener(Aurora.plugin, this);
        Aurora.protocolManager.addPacketListener(laserListener);
        Bukkit.getPluginManager().registerEvents(laserListener, Aurora.plugin);
    }

    @Override
    public void execute(EffectAction action) {
        if (action == EffectAction.START) {
            runTask(() -> laser.start(Aurora.plugin));
        } else if (action == EffectAction.STOP) {
            runTask(() -> laser.stop());
        }
    }

    @Override
    public void cleanup() {
        runTask(() -> {
            Aurora.protocolManager.removePacketListener(laserListener);
            HandlerList.unregisterAll(laserListener);
        });
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
                } catch (ReflectiveOperationException ignored) {
                }
            }
        }

        @EventHandler
        public void onPlayerLeave(PlayerQuitEvent event) {
            activePlayers.remove(event.getPlayer());
        }
    }
}
