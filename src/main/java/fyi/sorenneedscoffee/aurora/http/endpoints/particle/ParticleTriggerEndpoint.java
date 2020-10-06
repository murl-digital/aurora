package fyi.sorenneedscoffee.aurora.http.endpoints.particle;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.particle.ParticleModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class ParticleTriggerEndpoint extends Endpoint {

    public ParticleTriggerEndpoint() {
        this.path = Pattern.compile("/effects/particle/.+/trigger");
    }

    private Response trigger(UUID id, ParticleModel[] models) {
        try {
            EffectGroup group;
            try {
                group = ParticleCommon.constructGroup(id, models, true);
            } catch (IllegalArgumentException e) {
                return UNPROCESSABLE_ENTITY;
            } catch (NullPointerException e) {
                return POINT_DOESNT_EXIST;
            }

            EffectManager.triggerEffect(group);
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
            ParticleModel[] models = Aurora.gson.fromJson(bodyStream, ParticleModel[].class);
            return trigger(id, models);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
