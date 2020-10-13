package fyi.sorenneedscoffee.aurora.http.endpoints.particle;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.particle.ParticleModel;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ParticleStartEndpoint extends Endpoint {

  public ParticleStartEndpoint() {
    this.path = Pattern.compile(
        "/effects/particle/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
  }

  private Response start(UUID id, ParticleModel[] models) {
    try {
      if (EffectManager.exists(id)) {
        return BAD_REQUEST;
      }

      EffectGroup group;

      try {
        group = ParticleCommon.constructGroup(id, models, false);
      } catch (IllegalArgumentException e) {
        return UNPROCESSABLE_ENTITY;
      } catch (NullPointerException e) {
        return POINT_DOESNT_EXIST;
      }

      EffectManager.startEffect(group);
      return OK;
    } catch (Throwable e) {
      if (e.getMessage() != null) {
        Aurora.logger.warning(e.getMessage());
      }
      Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
      String message =
          e.getMessage() != null ? e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)
              : ExceptionUtils.getStackTrace(e);
      return SERVER_ERROR.clone().entity(message).build();
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    try {
      UUID id = UUID.fromString(tokens[2]);
      ParticleModel[] models = Aurora.gson.fromJson(bodyStream, ParticleModel[].class);
      return start(id, models);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }
}
