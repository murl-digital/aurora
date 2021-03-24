package digital.murl.aurora.http.endpoints.time;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.effects.time.TimeShiftEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.http.models.time.TimeShiftModel;
import digital.murl.aurora.managers.EffectManager;
import digital.murl.aurora.points.Point;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class TimeShiftStartEndpoint extends Endpoint {

  public TimeShiftStartEndpoint() {
    this.path = Pattern.compile(
        "/effects/time/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
  }

  private Response start(UUID id, TimeShiftModel[] models) {
    try {
        if (EffectManager.exists(id)) {
            return BAD_REQUEST;
        }

      Point point = Aurora.pointUtil.getPoint(0);
        if (point == null) {
          return POINT_DOESNT_EXIST;
        }

      EffectGroup group = new EffectGroup(id);
      for (TimeShiftModel model : models) {
        group.add(new TimeShiftEffect(point, model.amount));
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
      TimeShiftModel[] models = Aurora.gson.fromJson(bodyStream, TimeShiftModel[].class);
      return start(id, models);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }
}
