package fyi.sorenneedscoffee.aurora.http.endpoints.bossbar;

import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.BarManager;

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
