package fyi.sorenneedscoffee.aurora.http.endpoints.lightning;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.CacheBehavior;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.lightning.LightningEffect;
import fyi.sorenneedscoffee.aurora.http.models.lightning.LightningModel;
import fyi.sorenneedscoffee.aurora.points.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LightningCommon {

  public static EffectGroup constructGroup(UUID id, LightningModel[] models) {
    EffectGroup group = new EffectGroup(id, CacheBehavior.DISABLED);

    for (LightningModel model : models) {
      List<Point> points = new ArrayList<>();
      for (int pointId : model.pointIds) {
        points.add(Aurora.pointUtil.getPoint(pointId));
      }
      group.add(new LightningEffect(points.toArray(new Point[0]), model.spigotStrike));
    }
    return group;
  }
}
