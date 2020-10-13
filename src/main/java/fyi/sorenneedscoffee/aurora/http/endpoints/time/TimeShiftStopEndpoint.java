package fyi.sorenneedscoffee.aurora.http.endpoints.time;

import fyi.sorenneedscoffee.aurora.effects.time.TimeShiftEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class TimeShiftStopEndpoint extends Endpoint {

  public TimeShiftStopEndpoint() {
    this.path = Pattern.compile("/effects/time/stop");
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    EffectManager.stopType(TimeShiftEffect.class);
    return OK;
  }
}
