package digital.murl.aurora.http.endpoints.potion;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.annotations.StaticAction;
import digital.murl.aurora.annotations.StaticEffect;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.effects.potion.PotionEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.http.models.potion.PotionModel;
import digital.murl.aurora.managers.EffectManager;
import digital.murl.aurora.points.Point;
import org.bukkit.potion.PotionEffectType;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

@StaticEffect("potion")
public class PotionStartEndpoint extends Endpoint {

  public PotionStartEndpoint() {
    this.path = Pattern.compile(
        "/effects/potion/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
  }

  private static Response constructGroup(PotionModel[] models, Point point, EffectGroup group)
      throws Throwable {
    for (PotionModel model : models) {
      PotionEffectType type = PotionEffectType.getByName(model.potionType);
      if (type == null) {
        return UNPROCESSABLE_ENTITY;
      }
      group.add(new PotionEffect(point, type, model.amplifier));
    }

    EffectManager.startEffect(group);
    return OK;
  }

  private Response start(UUID id, PotionModel[] models) {
    try {
      if (EffectManager.exists(id)) {
        return BAD_REQUEST;
      }

      Point point = Aurora.pointUtil.getPoint(0);
      if (point == null) {
        return POINT_DOESNT_EXIST;
      }

      EffectGroup group = new EffectGroup(id);
      return constructGroup(models, point, group);
    } catch (Throwable throwable) {
      return getErrorResponse(throwable);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    try {
      UUID id = UUID.fromString(tokens[2]);
      PotionModel[] models = Aurora.gson.fromJson(bodyStream, PotionModel[].class);
      return start(id, models);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }

  @StaticAction
  public Response startStatic(PotionModel[] models) {
    try {
      Point point = Aurora.pointUtil.getPoint(0);
      if (point == null) {
        return POINT_DOESNT_EXIST;
      }

      EffectGroup group = new EffectGroup(UUID.randomUUID(), true);
      return constructGroup(models, point, group);
    } catch (Throwable throwable) {
      return getErrorResponse(throwable);
    }
  }
}
