package digital.murl.aurora.http.endpoints.laser;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.effects.laser.LaserEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.http.models.laser.LaserModel;
import digital.murl.aurora.managers.EffectManager;
import digital.murl.aurora.points.Point;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class LaserStartEndpoint extends Endpoint {

  public LaserStartEndpoint() {
    this.path = Pattern.compile(
        "/effects/laser/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
  }

  public Response start(UUID id, LaserModel[] request) {
    try {
      if (EffectManager.exists(id)) {
        return BAD_REQUEST;
      }

      EffectGroup group = new EffectGroup(id);
      for (LaserModel model : request) {
        Point start = Aurora.pointUtil.getPoint(model.startId);
        Point end = Aurora.pointUtil.getPoint(model.endId);
        if (start == null || end == null) {
          return POINT_DOESNT_EXIST;
        }
        group.add(new LaserEffect(start, end));
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
      LaserModel[] models = Aurora.gson.fromJson(bodyStream, LaserModel[].class);
      return start(id, models);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }
}
