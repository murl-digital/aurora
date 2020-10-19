package fyi.sorenneedscoffee.aurora.http.endpoints.lightning;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.lightning.LightningModel;
import fyi.sorenneedscoffee.aurora.managers.EffectManager;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.commons.lang.exception.ExceptionUtils;

public class LightningTriggerEndpoint extends Endpoint {

  public LightningTriggerEndpoint() {
    this.path = Pattern.compile(
        "/effects/lightning/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/trigger");
  }

  private Response start(UUID id, LightningModel[] models) {
    EffectGroup group = LightningCommon.constructGroup(id, models);

    try {
      EffectManager.triggerEffect(group);
    } catch (Throwable throwable) {
      if (throwable.getMessage() != null) {
        Aurora.logger.warning(throwable.getMessage());
      }
      Aurora.logger.warning(ExceptionUtils.getStackTrace(throwable));
      String message =
          throwable.getMessage() != null ? throwable.getMessage() + "\n\n" + ExceptionUtils
              .getStackTrace(throwable)
              : ExceptionUtils.getStackTrace(throwable);
      return SERVER_ERROR.clone().entity(message).build();
    }

    return OK;
  }

  @Override
  public Response handle(String[] tokens, InputStreamReader bodyStream) {
    UUID id = UUID.fromString(tokens[2]);
    LightningModel[] models = Aurora.gson.fromJson(bodyStream, LightningModel[].class);
    return start(id, models);
  }
}
