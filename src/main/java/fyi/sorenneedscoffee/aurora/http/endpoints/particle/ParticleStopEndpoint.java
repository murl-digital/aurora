package fyi.sorenneedscoffee.aurora.http.endpoints.particle;

import fyi.sorenneedscoffee.aurora.effects.particle.ParticleEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class ParticleStopEndpoint extends Endpoint {

  public ParticleStopEndpoint() {
    this.path = Pattern.compile("/effects/particle/stop");
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    EffectManager.stopType(ParticleEffect.class);
    return OK;
  }
}
