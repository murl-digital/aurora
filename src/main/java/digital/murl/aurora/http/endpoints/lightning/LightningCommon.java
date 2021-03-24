package digital.murl.aurora.http.endpoints.lightning;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.CacheBehavior;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.effects.lightning.LightningEffect;
import digital.murl.aurora.http.models.lightning.LightningModel;
import digital.murl.aurora.points.Point;

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
