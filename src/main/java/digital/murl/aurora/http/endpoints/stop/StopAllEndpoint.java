package digital.murl.aurora.http.endpoints.stop;

import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

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
