package digital.murl.aurora.http.endpoints.bossbar;

import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.BarManager;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class BossBarClearEndpoint extends Endpoint {

  public BossBarClearEndpoint() {
    this.path = Pattern.compile("/bar/clear");
  }

  public Response clear() {
    try {
      if (!BarManager.isActive()) {
        return OK;
      }

      BarManager.clearBars();
      return OK;
    } catch (Exception e) {
      return getErrorResponse(e);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    return clear();
  }
}
