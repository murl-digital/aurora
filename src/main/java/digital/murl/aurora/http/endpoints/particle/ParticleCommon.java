package digital.murl.aurora.http.endpoints.particle;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.effects.particle.ParticleEffect;
import digital.murl.aurora.effects.particle.Region;
import digital.murl.aurora.http.models.particle.ParticleModel;
import digital.murl.aurora.points.Point;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParticleCommon {

  public static EffectGroup constructGroup(UUID id, ParticleModel[] models,
                                           boolean ignoreRandomized) throws IllegalArgumentException, NullPointerException {
    EffectGroup group = new EffectGroup(id);
    for (ParticleModel model : models) {
      List<Point> points = new ArrayList<>();
      for (int i : model.region.pointIds) {
        Point point = Aurora.pointUtil.getPoint(i);
        if (point == null) {
          throw new NullPointerException();
        }
        points.add(Aurora.pointUtil.getPoint(i));
      }
      Region region = new Region(
          points.toArray(new Point[0]),
          model.region.type,
          model.region.density,
          !ignoreRandomized && model.region.randomized,
          model.region.equation,
          model.region.quantity
      );
      Particle particle;
      particle = Particle.valueOf(model.name);
      Object options = null;
      switch (particle) {
        case REDSTONE:
          Color color = Color.fromRGB(model.options.dustColor[0], model.options.dustColor[1], model.options.dustColor[2]);
          options = new Particle.DustOptions(color, model.options.dustSize);
          break;
        case DUST_COLOR_TRANSITION:
          Color startColor = Color.fromRGB(model.options.dustColor[0], model.options.dustColor[1], model.options.dustColor[2]);
          Color endColor = Color.fromRGB(model.options.toColor[0], model.options.toColor[1], model.options.toColor[2]);
          options = new Particle.DustTransition(startColor, endColor, model.options.dustSize);
          break;
        case ITEM_CRACK:
          Material material = Material.valueOf(model.options.materialName);
          if (!material.isItem()) {
            throw new IllegalArgumentException();
          }
          options = new ItemStack(material);
          break;
        case BLOCK_CRACK:
        case BLOCK_DUST:
        case FALLING_DUST:
          Material material1 = Material.valueOf(model.options.materialName);
          options = material1.createBlockData();
          break;
      }
      ParticleEffect effect = new ParticleEffect(region, particle, options);
      group.add(effect);
    }

    return group;
  }
}
