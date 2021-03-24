package digital.murl.aurora.http.endpoints.time;

import digital.murl.aurora.effects.time.TimeShiftEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

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
