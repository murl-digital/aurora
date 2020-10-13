package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.effects.laser.LaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class LaserStopEndpoint extends Endpoint {

  public LaserStopEndpoint() {
    this.path = Pattern.compile("/effects/laser/stop");
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    EffectManager.stopType(LaserEffect.class);
    return OK;
  }
}
