package digital.murl.aurora.http.endpoints.laser;

import digital.murl.aurora.effects.laser.EndLaserEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

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
