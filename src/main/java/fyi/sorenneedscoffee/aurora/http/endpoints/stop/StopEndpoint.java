package fyi.sorenneedscoffee.aurora.http.endpoints.stop;

import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class StopEndpoint extends Endpoint {

  public StopEndpoint() {
    this.path = Pattern.compile(
        "/effects/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/stop");
  }

  private Response stop(UUID id) {
    try {
      if (!EffectManager.exists(id)) {
        return NOT_FOUND;
      }

      EffectManager.stopEffect(id);
      return OK;
    } catch (Exception e) {
      return getErrorResponse(e);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader ignore) {
    try {
      UUID id = UUID.fromString(tokens[1]);
      return stop(id);
    } catch (IllegalArgumentException e) {
      return BAD_REQUEST;
    }
  }
}
