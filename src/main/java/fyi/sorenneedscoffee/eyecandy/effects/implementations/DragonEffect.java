package fyi.sorenneedscoffee.eyecandy.effects.implementations;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.Effect;
import fyi.sorenneedscoffee.eyecandy.wrapper.WrapperPlayServerEntityStatus;
import fyi.sorenneedscoffee.eyecandy.wrapper.WrapperPlayServerNamedSoundEffect;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DragonEffect implements Effect {
    protected static boolean active = false;
    protected static EnderDragon dragon;
    protected static ArmorStand stand;

    public static void execute(Location loc) {
        if (!active) {
            active = true;
            dragon = loc.getWorld().spawn(loc, EnderDragon.class);
            dragon.setSilent(true);
            stand = loc.getWorld().spawn(loc.subtract(0, 2.0, 0), ArmorStand.class);
            stand.setGravity(false);
            stand.setMarker(true);
            stand.setVisible(false);
            stand.addPassenger(dragon);

            WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
            packet.setEntityID(dragon.getEntityId());
            packet.setEntityStatus((byte) 3);
            EyeCandy.manager.broadcastServerPacket(packet.getHandle());
        } else {
            active = false;
            dragon.remove();
            stand.remove();
            DragonPacketListener.clear();
        }
    }

    public static class DragonListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            if (active) {
                DragonPacketListener.watch(event.getPlayer().getUniqueId());
            }
        }
    }

    public static class DragonPacketListener extends PacketAdapter {
        private static final List<UUID> watchList = new ArrayList<>();

        public DragonPacketListener(Plugin plugin) {
            super(plugin,
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.NAMED_SOUND_EFFECT,
                    PacketType.Play.Client.POSITION
            );
        }

        protected static void watch(UUID uuid) {
            watchList.add(uuid);
        }

        protected static void clear() {
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
                        EyeCandy.manager.sendServerPacket(event.getPlayer(), packet.getHandle());
                    } catch (InvocationTargetException e) {
                        Bukkit.getLogger().severe(ExceptionUtils.getFullStackTrace(e));
                    }

                    watchList.remove(event.getPlayer().getUniqueId());
                }
            }
        }
    }
}
