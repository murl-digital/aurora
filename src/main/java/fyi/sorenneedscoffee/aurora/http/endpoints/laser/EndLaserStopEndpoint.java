package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.effects.laser.EndLaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class EndLaserStopEndpoint extends Endpoint {

  public EndLaserStopEndpoint() {
    this.path = Pattern.compile("/effects/endlaser/stop");
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    EffectManager.stopType(EndLaserEffect.class);
    return OK;
  }
}
