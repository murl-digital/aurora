package digital.murl.aurora.http.endpoints.laser;

import digital.murl.aurora.effects.laser.LaserEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

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
