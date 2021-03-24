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

public class ParticleTriggerEndpoint extends Endpoint {

  public ParticleTriggerEndpoint() {
    this.path = Pattern.compile(
        "/effects/particle/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/trigger");
  }

  private Response trigger(UUID id, ParticleModel[] models) {
    try {
      EffectGroup group;
      try {
        group = ParticleCommon.constructGroup(id, models, true);
      } catch (IllegalArgumentException e) {
        return UNPROCESSABLE_ENTITY;
      } catch (NullPointerException e) {
        return POINT_DOESNT_EXIST;
      }

      EffectManager.triggerEffect(group);
      return OK;
    } catch (Exception e) {
      return getErrorResponse(e);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    try {
      UUID id = UUID.fromString(tokens[2]);
      ParticleModel[] models = Aurora.gson.fromJson(bodyStream, ParticleModel[].class);
      return trigger(id, models);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }
}
