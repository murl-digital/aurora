package fyi.sorenneedscoffee.eyecandy.effects.implementations;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;
import fyi.sorenneedscoffee.eyecandy.effects.Toggleable;
import fyi.sorenneedscoffee.eyecandy.util.Point;
import fyi.sorenneedscoffee.eyecandy.wrapper.WrapperPlayServerEntityStatus;
import fyi.sorenneedscoffee.eyecandy.wrapper.WrapperPlayServerNamedSoundEffect;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
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

public class DragonEffect implements Toggleable {
    protected static boolean active = false;
    protected static EnderDragon dragon;
    protected static ArmorStand stand;
    private DragonEffect.DragonListener dragonListener;
    public Point point;
    public boolean isStatic = true;

    @Override
    public void init(Point point) {
        dragonListener = new DragonEffect.DragonListener(EyeCandy.plugin);
        EyeCandy.protocolManager.addPacketListener(dragonListener);
        getServer().getPluginManager().registerEvents(dragonListener, EyeCandy.plugin);
        this.point = point;
    }

    @Override
    public void cleanup() {
        EyeCandy.protocolManager.removePacketListener(dragonListener);
        HandlerList.unregisterAll(dragonListener);
    }

    @Override
    public void execute(EffectAction action) {
        Bukkit.getScheduler().runTask(EyeCandy.plugin, () -> {
            if(action == EffectAction.START) {
                Location loc = point.location;
                active = true;
                dragon = (EnderDragon) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
                dragon.setSilent(true);
                if(isStatic) {
                    stand = loc.getWorld().spawn(loc.subtract(0, 2.0, 0), ArmorStand.class);
                    stand.setGravity(false);
                    stand.setMarker(true);
                    stand.setVisible(false);
                    stand.addPassenger(dragon);
                }

                WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
                packet.setEntityID(dragon.getEntityId());
                packet.setEntityStatus((byte) 3);
                EyeCandy.protocolManager.broadcastServerPacket(packet.getHandle());
            } else if(action == EffectAction.STOP) {
                active = false;
                dragon.remove();
                stand.remove();
                dragonListener.clear();
            } else if(action == EffectAction.RESTART) {
                WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
                packet.setEntityID(dragon.getEntityId());
                packet.setEntityStatus((byte) 3);
                EyeCandy.protocolManager.broadcastServerPacket(packet.getHandle());
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DragonEffect) {
            return ((DragonEffect) obj).point.equals(point);
        }

        return false;
    }

    public static class DragonListener extends PacketAdapter implements Listener {
        private static final List<UUID> watchList = new ArrayList<>();

        public DragonListener(Plugin plugin) {
            super(plugin,
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.NAMED_SOUND_EFFECT,
                    PacketType.Play.Client.POSITION
            );
        }

        protected void watch(UUID uuid) {
            watchList.add(uuid);
        }

        protected void clear() {
            watchList.clear();
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (DragonEffect.active) {
                if (event.getPacketType() == PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    WrapperPlayServerNamedSoundEffect packet = new WrapperPlayServerNamedSoundEffect(event.getPacket());

                    if (packet.getSoundEffect().name().contains("ENDER_DRAGON"))
                        event.setCancelled(true);
                }
            }
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            if (!watchList.isEmpty() && event.getPacketType() == PacketType.Play.Client.POSITION) {
                if (watchList.contains(event.getPlayer().getUniqueId())) {
                    WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
                    packet.setEntityID(dragon.getEntityId());
                    packet.setEntityStatus((byte) 3);
                    try {
                        EyeCandy.protocolManager.sendServerPacket(event.getPlayer(), packet.getHandle());
                    } catch (InvocationTargetException e) {
                        Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(e));
                    }

                    watchList.remove(event.getPlayer().getUniqueId());
                }
            }
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            if (active) {
                watch(event.getPlayer().getUniqueId());
            }
        }
    }
}
