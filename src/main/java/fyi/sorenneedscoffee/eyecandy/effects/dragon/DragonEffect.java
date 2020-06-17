package fyi.sorenneedscoffee.eyecandy.effects.dragon;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.Effect;
import fyi.sorenneedscoffee.eyecandy.effects.EffectAction;
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

public class DragonEffect extends Effect {
    protected static boolean active = false;
    public final Point point;
    public final boolean isStatic;
    protected ArmorStand stand;
    protected EnderDragon dragon;
    private DragonListener dragonListener;

    public DragonEffect(Point point, boolean isStatic) {
        this.point = point;
        this.isStatic = isStatic;
    }

    @Override
    public void init() {
        dragonListener = new DragonListener(EyeCandy.plugin, this);
        EyeCandy.protocolManager.addPacketListener(dragonListener);
        getServer().getPluginManager().registerEvents(dragonListener, EyeCandy.plugin);
    }

    @Override
    public void cleanup() {
        EyeCandy.protocolManager.removePacketListener(dragonListener);
        HandlerList.unregisterAll(dragonListener);
        dragonListener.clear();
    }

    @Override
    public void execute(EffectAction action) {
        if (action == EffectAction.START) {
            Bukkit.getScheduler().runTask(EyeCandy.plugin, () -> {
                Location loc = point.getLocation();
                active = true;
                dragon = (EnderDragon) loc.getWorld().spawnEntity(loc, EntityType.ENDER_DRAGON);
                dragon.setSilent(true);

                stand = null;
                if (isStatic) {
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
            });
        } else if (action == EffectAction.STOP) {
            Bukkit.getScheduler().runTask(EyeCandy.plugin, () -> {
                active = false;

                dragon.remove();
                if(stand != null)
                    stand.remove();
            });
        } else if (action == EffectAction.RESTART) {
            Bukkit.getScheduler().runTask(EyeCandy.plugin, () -> {
                WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
                packet.setEntityID(dragon.getEntityId());
                packet.setEntityStatus((byte) 3);
                EyeCandy.protocolManager.broadcastServerPacket(packet.getHandle());
            });
        }
    }

    private static class DragonListener extends PacketAdapter implements Listener {
        private final List<UUID> watchList = new ArrayList<>();
        private final DragonEffect effect;

        public DragonListener(Plugin plugin, DragonEffect effect) {
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
                    packet.setEntityID(effect.dragon.getEntityId());
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
