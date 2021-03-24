package digital.murl.aurora.http.endpoints.lightning;

import digital.murl.aurora.Aurora;
import digital.murl.aurora.effects.EffectGroup;
import digital.murl.aurora.http.Endpoint;
import digital.murl.aurora.http.Response;
import digital.murl.aurora.http.models.lightning.LightningModel;
import digital.murl.aurora.managers.EffectManager;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class LightningStartEndpoint extends Endpoint {

  public LightningStartEndpoint() {
    this.path = Pattern.compile(
        "/effects/lightning/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
  }

  private Response start(UUID id, LightningModel[] models) {
    try {
      EffectGroup group = LightningCommon.constructGroup(id, models);
      EffectManager.startEffect(group);
      return OK;
    } catch (Throwable throwable) {
      return getErrorResponse(throwable);
    }
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    UUID id = UUID.fromString(tokens[2]);
    LightningModel[] models = Aurora.gson.fromJson(bodyStream, LightningModel[].class);
    return start(id, models);
  }
}
