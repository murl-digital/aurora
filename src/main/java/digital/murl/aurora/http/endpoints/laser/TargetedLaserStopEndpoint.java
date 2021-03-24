package digital.murl.aurora.http.endpoints.laser;

import digital.murl.aurora.effects.laser.TargetedLaserEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class TargetedLaserStopEndpoint extends Endpoint {

  public TargetedLaserStopEndpoint() {
    this.path = Pattern.compile("/effects/targetedlaser/stop");
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    EffectManager.stopType(TargetedLaserEffect.class);
    return OK;
  }
}
