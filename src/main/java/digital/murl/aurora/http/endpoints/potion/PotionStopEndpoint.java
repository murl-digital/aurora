package digital.murl.aurora.http.endpoints.potion;

import digital.murl.aurora.effects.potion.PotionEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class PotionStopEndpoint extends Endpoint {

  public PotionStopEndpoint() {
    this.path = Pattern.compile("/effects/potion/stop");
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    EffectManager.stopType(PotionEffect.class);
    return OK;
  }
}
