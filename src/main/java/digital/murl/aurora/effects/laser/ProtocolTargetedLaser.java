package digital.murl.aurora.effects.laser;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.wrapper.WrapperPlayServerEntityDestroy;
import digital.murl.aurora.wrapper.WrapperPlayServerEntityMetadata;
import digital.murl.aurora.wrapper.WrapperPlayServerSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtocolTargetedLaser {

  private final WrapperPlayServerSpawnEntityLiving spawnGuardianPacket;
  //dont ask.
  private final WrapperPlayServerEntityDestroy theEndIsNigh;
  private boolean isActive = false;
  private WrapperPlayServerEntityMetadata guardianMetadataPacket;


  public ProtocolTargetedLaser(Location start, Player target) {

    this.spawnGuardianPacket = LaserPacketFactory.createGuardianPacket(start);
    this.guardianMetadataPacket = LaserPacketFactory
        .createGuardianMetaPacket(spawnGuardianPacket.getEntityID(), target.getEntityId());
    this.theEndIsNigh = LaserPacketFactory
        .createDestroyPacket(new int[]{spawnGuardianPacket.getEntityID()});
  }

  public void start() {
    this.isActive = true;
    Aurora.protocolManager.broadcastServerPacket(this.spawnGuardianPacket.getHandle());
    Aurora.protocolManager.broadcastServerPacket(this.guardianMetadataPacket.getHandle());
  }

  public void stop() {
    this.isActive = false;
    Aurora.protocolManager.broadcastServerPacket(theEndIsNigh.getHandle());
  }

  public void changeTarget(Player target) {
    guardianMetadataPacket = LaserPacketFactory
        .createGuardianMetaPacket(spawnGuardianPacket.getEntityID(), target.getEntityId());
    if (isActive) {
      Aurora.protocolManager.broadcastServerPacket(guardianMetadataPacket.getHandle());
    }
  }

  public boolean isStarted() {
    return isActive;
  }

  public void sendStartPackets(Player p) {
    if (!p.isOnline()) {
      return;
    }
    Aurora.logger.info("" + guardianMetadataPacket.getMetadata().get(16).getValue());

    try {
      Aurora.protocolManager.sendServerPacket(p, spawnGuardianPacket.getHandle());
      Aurora.protocolManager.sendServerPacket(p, guardianMetadataPacket.getHandle());
    } catch (Exception ignore) {
    }
  }

  public void callColorChange() {
    Aurora.protocolManager.broadcastServerPacket(guardianMetadataPacket.getHandle());
  }
}
