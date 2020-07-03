package fyi.sorenneedscoffee.aurora.effects.bossbar;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.Effect;
import fyi.sorenneedscoffee.aurora.effects.EffectAction;
import fyi.sorenneedscoffee.aurora.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerBoss;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerEntityStatus;
import fyi.sorenneedscoffee.aurora.wrapper.WrapperPlayServerNamedSoundEffect;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class BossBarEffect extends Effect {
    protected WrapperPlayServerBoss packet = new WrapperPlayServerBoss();
    private BossBarListener bossBarListener;

    public BossBarEffect(UUID id, BarColor color, String title) {
        packet.setUniqueId(id);
        packet.setTitle(WrappedChatComponent.fromText(title));
        packet.setColor(color);
    }

    @Override
    public void init() {
        packet.setAction(WrapperPlayServerBoss.Action.ADD);
        packet.setStyle(WrapperPlayServerBoss.BarStyle.PROGRESS);
        packet.setHealth(1.0f);
        packet.setCreateFog(false);
        packet.setDarkenSky(false);
        packet.setPlayMusic(false);

        bossBarListener = new BossBarListener(Aurora.plugin, this);
    }

    @Override
    public void execute(EffectAction action) {
        if (action == EffectAction.START) {
            Bukkit.getScheduler().runTask(Aurora.plugin, () -> Aurora.protocolManager.broadcastServerPacket(packet.getHandle()));
            Aurora.protocolManager.addPacketListener(bossBarListener);
            getServer().getPluginManager().registerEvents(bossBarListener, Aurora.plugin);
        } else if (action == EffectAction.STOP) {
            packet.setAction(WrapperPlayServerBoss.Action.REMOVE);
            Aurora.protocolManager.broadcastServerPacket(packet.getHandle());
            Aurora.protocolManager.removePacketListener(bossBarListener);
            HandlerList.unregisterAll(bossBarListener);
            bossBarListener.clear();
        }
    }

    @Override
    public void cleanup() {

    }

    private static class BossBarListener extends PacketAdapter implements Listener {
        private final List<UUID> watchList = new ArrayList<>();
        private final BossBarEffect effect;

        public BossBarListener(Plugin plugin, BossBarEffect effect) {
            super(plugin,
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.NAMED_SOUND_EFFECT,
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
                    effect.packet.setAction(WrapperPlayServerBoss.Action.ADD);
                    try {
                        Aurora.protocolManager.sendServerPacket(event.getPlayer(), effect.packet.getHandle());
                    } catch (InvocationTargetException e) {
                        Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(e));
                    }

                    watchList.remove(event.getPlayer().getUniqueId());
                }
            }
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            watch(event.getPlayer().getUniqueId());
        }
    }
}
