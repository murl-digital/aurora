package fyi.sorenneedscoffee.aurora.http.endpoints.bossbar;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.bossbar.BossBarEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.bossbar.BossBarModel;
import fyi.sorenneedscoffee.aurora.managers.BarManager;
import fyi.sorenneedscoffee.aurora.points.Point;
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
