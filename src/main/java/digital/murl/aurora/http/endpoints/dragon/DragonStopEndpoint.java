package digital.murl.aurora.http.endpoints.dragon;

import digital.murl.aurora.effects.dragon.DragonEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class DragonStopEndpoint extends Endpoint {

  public DragonStopEndpoint() {
    this.path = Pattern.compile("/effects/dragon/stop");
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    EffectManager.stopType(DragonEffect.class);
    return OK;
  }
}
