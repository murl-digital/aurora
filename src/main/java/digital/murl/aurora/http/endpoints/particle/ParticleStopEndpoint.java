package digital.murl.aurora.http.endpoints.particle;

import digital.murl.aurora.effects.particle.ParticleEffect;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.managers.EffectManager;

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
