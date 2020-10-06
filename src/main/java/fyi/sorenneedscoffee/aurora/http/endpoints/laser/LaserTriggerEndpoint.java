package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.laser.LaserEffect;
import fyi.sorenneedscoffee.aurora.effects.laser.TargetedLaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class LaserTriggerEndpoint extends Endpoint {

    public LaserTriggerEndpoint() {
        this.path = Pattern.compile("/effects/(laser|targetedlaser)/.+/trigger");
    }

    public static Response trigger(UUID id) {
        try {
            if (!EffectManager.instanceOf(id, LaserEffect.class) && !EffectManager.instanceOf(id, TargetedLaserEffect.class))
                return BAD_REQUEST;

            EffectManager.hotTriggerEffect(id);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        try {
            UUID id = UUID.fromString(tokens[2]);
            return trigger(id);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
