package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.TargetedLaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.laser.TargetedLaserModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class TargetedLaserStartEndpoint extends Endpoint {

    public TargetedLaserStartEndpoint() {
        this.path = Pattern.compile("/effects/targetedlaser/[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}/start");
    }

    public static Response start(UUID id, TargetedLaserModel[] models) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

            EffectGroup group = new EffectGroup(id);
            for (TargetedLaserModel model : models) {
                Point start = Aurora.pointUtil.getPoint(model.startId);
                if (start == null)
                    return POINT_DOESNT_EXIST;
                group.add(new TargetedLaserEffect(start));
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity("There are no players online.").build();
        } catch (Throwable e) {
            if (e.getMessage() != null) Aurora.logger.warning(e.getMessage());
            Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
            String message = e.getMessage() != null ? e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e) : ExceptionUtils.getStackTrace(e);
            return SERVER_ERROR.clone().entity(message).build();
        }
    }

    public static Response restart(UUID id) {
        try {
            if (!EffectManager.instanceOf(id, TargetedLaserEffect.class))
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
            switch (tokens[3]) {
                case "start":
                    TargetedLaserModel[] models = Aurora.gson.fromJson(bodyStream, TargetedLaserModel[].class);
                    return start(id, models);
                case "restart":
                    return restart(id);
                default:
                    return NOT_FOUND;
            }
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
