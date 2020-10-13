package fyi.sorenneedscoffee.aurora.effects.laser;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.points.Point;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TargetedLaserEffect extends Effect {
    private final Point start;
    protected ProtocolTargetedLaser targetedLaser;
    private TargetedLaserEffect.LaserListener laserListener;

    public TargetedLaserEffect(Point start) {
        this.start = start;
    }

    @Override
    public void init() throws Exception {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.size() == 0) throw new IllegalArgumentException();
        Player player = onlinePlayers.get(Aurora.random.nextInt(onlinePlayers.size()));

        targetedLaser = new ProtocolTargetedLaser(
                start.getLocation(),
                player
        );
        laserListener = new TargetedLaserEffect.LaserListener(Aurora.plugin, this);
        Aurora.protocolManager.addPacketListener(laserListener);
        Bukkit.getPluginManager().registerEvents(laserListener, Aurora.plugin);
    }

    @Override
    public void execute(EffectAction action) {
        switch (action) {
            case START:
                runTask(() -> targetedLaser.start());
                break;
            case RESTART:
                runTask(() -> {
                    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
                    try {
                        targetedLaser.changeTarget(onlinePlayers.get(Aurora.random.nextInt(onlinePlayers.size())));
                    } catch (Exception ignored) {
                    }
                });
                break;
            case TRIGGER:
                runTask(() -> {
                    try {
                        targetedLaser.callColorChange();
                    } catch (Exception e) {
                        Aurora.logger.warning("An error occurred while attempting to change a laser's color.");
                        Aurora.logger.warning(e.getMessage());
                        Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
                    }
                });
                break;
            case STOP:
                runTask(() -> targetedLaser.stop());
                break;
        }
    }

    @Override
    public void cleanup() {

    }

    private static class LaserListener extends PacketAdapter implements Listener {
        private final List<Player> activePlayers;
        private final TargetedLaserEffect effect;

        protected LaserListener(Plugin plugin, TargetedLaserEffect effect) {
            super(plugin,
                    ListenerPriority.NORMAL,
                    PacketType.Play.Client.POSITION
            );
            this.effect = effect;
            activePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (effect.targetedLaser.isStarted() && !activePlayers.contains(event.getPlayer())) {
                try {
                    activePlayers.add(event.getPlayer());
                    effect.targetedLaser.sendStartPackets(event.getPlayer());
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
