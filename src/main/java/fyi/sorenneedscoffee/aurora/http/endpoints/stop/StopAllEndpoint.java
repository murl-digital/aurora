package fyi.sorenneedscoffee.aurora.http.endpoints.stop;

import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class StopAllEndpoint extends Endpoint {

  public StopAllEndpoint() {
    this.path = Pattern.compile("/effects/all/stop");
  }

  private Response stopAll() {
    try {
      EffectManager.stopAll(false);
      return OK;
    } catch (Exception e) {
      return getErrorResponse(e);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    return stopAll();
  }
}
