package digital.murl.aurora.http.endpoints.particle;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.http.models.particle.ParticleModel;
import digital.murl.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

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
    } catch (Throwable throwable) {
      return getErrorResponse(throwable);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    try {
      UUID id = UUID.fromString(tokens[2]);
      ParticleModel[] models = Aurora.gson.fromJson(bodyStream, ParticleModel[].class);
      return start(id, models);
    } catch (IllegalArgumentException e) {
      Aurora.logger.warning("Something went wrong. Bad payload? " + e.toString());
      return BAD_REQUEST;
    }
  }
}
