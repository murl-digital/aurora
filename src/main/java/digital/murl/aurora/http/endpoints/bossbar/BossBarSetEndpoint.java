package digital.murl.aurora.http.endpoints.bossbar;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.effects.bossbar.BossBarEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.http.models.bossbar.BossBarModel;
import digital.murl.aurora.managers.BarManager;
import digital.murl.aurora.points.Point;
import org.bukkit.boss.BarColor;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class BossBarSetEndpoint extends Endpoint {

  public BossBarSetEndpoint() {
    this.path = Pattern.compile("/bar/set");
  }

  public Response set(BossBarModel[] models) {
    try {
      Point point = Aurora.pointUtil.getPoint(0);
      if (point == null) {
        return POINT_DOESNT_EXIST;
      }

      EffectGroup group = new EffectGroup(UUID.randomUUID());

      for (BossBarModel model : models) {
        BarColor color;
        try {
          color = BarColor.valueOf(model.color);
        } catch (IllegalArgumentException e) {
          return UNPROCESSABLE_ENTITY;
        }

        group.add(new BossBarEffect(UUID.randomUUID(), color, model.title));
      }

      if (BarManager.isActive()) {
        BarManager.clearBars();
      }
      BarManager.showBar(group);

      return OK;
    } catch (Exception e) {
      return getErrorResponse(e);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    BossBarModel[] models = Aurora.gson.fromJson(bodyStream, BossBarModel[].class);
    return set(models);
  }
}
