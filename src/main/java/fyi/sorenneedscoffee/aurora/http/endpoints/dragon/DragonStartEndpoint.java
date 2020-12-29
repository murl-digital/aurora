package fyi.sorenneedscoffee.aurora.http.endpoints.dragon;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.dragon.DragonModel;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;
import fyi.sorenneedscoffee.aurora.points.Point;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class DragonStartEndpoint extends Endpoint {

  public DragonStartEndpoint() {
    this.path = Pattern.compile(
        "/effects/dragon/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
  }

  public Response start(UUID id, DragonModel[] models) {
    try {
      if (EffectManager.exists(id)) {
        return BAD_REQUEST;
      }

      EffectGroup group = new EffectGroup(id);
      for (DragonModel model : models) {
        Point point = Aurora.pointUtil.getPoint(model.pointId);
        if (point == null) {
          return POINT_DOESNT_EXIST;
        }

        DragonEffect effect = new DragonEffect(point, model.isStatic);
        group.add(effect);
      }

      EffectManager.startEffect(group);
      return OK;
    } catch (Throwable throwable) {
      return getErrorResponse(throwable);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    try {
      UUID id = UUID.fromString(tokens[2]);
      DragonModel[] models = Aurora.gson.fromJson(bodyStream, DragonModel[].class);
      return start(id, models);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }
}
