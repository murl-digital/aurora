package digital.murl.aurora.http.endpoints.laser;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.effects.laser.TargetedLaserEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.http.models.laser.TargetedLaserModel;
import digital.murl.aurora.managers.EffectManager;
import digital.murl.aurora.points.Point;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class TargetedLaserStartEndpoint extends Endpoint {

  public TargetedLaserStartEndpoint() {
    this.path = Pattern.compile(
        "/effects/targetedlaser/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
  }

  public Response start(UUID id, TargetedLaserModel[] models) {
    try {
      if (EffectManager.exists(id)) {
        return BAD_REQUEST;
      }

      EffectGroup group = new EffectGroup(id);
      for (TargetedLaserModel model : models) {
        Point start = Aurora.pointUtil.getPoint(model.startId);
        if (start == null) {
          return POINT_DOESNT_EXIST;
        }
        group.add(new TargetedLaserEffect(start));
      }

      EffectManager.startEffect(group);
      return OK;
    } catch (IllegalArgumentException e) {
      return Response.status(400).entity("There are no players online.").build();
    } catch (Throwable throwable) {
      return getErrorResponse(throwable);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    try {
      UUID id = UUID.fromString(tokens[2]);
      TargetedLaserModel[] models = Aurora.gson.fromJson(bodyStream, TargetedLaserModel[].class);
      return start(id, models);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }
}
