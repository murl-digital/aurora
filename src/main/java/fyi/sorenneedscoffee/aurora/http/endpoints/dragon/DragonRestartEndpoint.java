package fyi.sorenneedscoffee.aurora.http.endpoints.dragon;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class DragonRestartEndpoint extends Endpoint {

    public DragonRestartEndpoint() {
        this.path = Pattern.compile("/effects/dragon/.+/(start|restart)");
    }

    public static Response restart(UUID id) {
        try {
            if (EffectManager.instanceOf(id, DragonEffect.class))
                return BAD_REQUEST;

            EffectManager.restartEffect(id);
            return OK;
        } catch (Exception e) {
            if (e.getMessage() != null) Aurora.logger.warning(e.getMessage());
            Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
            String message = e.getMessage() != null ? e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e) : ExceptionUtils.getStackTrace(e);
            return SERVER_ERROR.clone().entity(message).build();
        }
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        try {
            UUID id = UUID.fromString(tokens[2]);
            return restart(id);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
