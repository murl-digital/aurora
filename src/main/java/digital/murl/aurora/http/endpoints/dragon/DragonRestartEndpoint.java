package digital.murl.aurora.http.endpoints.dragon;

import digital.murl.aurora.effects.dragon.DragonEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class DragonRestartEndpoint extends Endpoint {

  public DragonRestartEndpoint() {
    this.path = Pattern.compile(
        "/effects/dragon/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/restart");
  }

  public Response restart(UUID id) {
    try {
      if (EffectManager.instanceOf(id, DragonEffect.class)) {
        return BAD_REQUEST;
      }

      EffectManager.restartEffect(id);
      return OK;
    } catch (Exception e) {
      return getErrorResponse(e);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    try {
      UUID id = UUID.fromString(tokens[2]);
      return restart(id);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }
}
