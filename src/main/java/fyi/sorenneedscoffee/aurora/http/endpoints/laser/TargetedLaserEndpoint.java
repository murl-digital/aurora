package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.TargetedLaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.laser.TargetedLaserModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class TargetedLaserEndpoint extends Endpoint {

    public TargetedLaserEndpoint() {
        this.path = Pattern.compile("/effects/targetedlaser/.+/(start|restart)");
    }

    @Operation(
            summary = "zap",
            description = "This effect also takes advantage of the guardian laser. Unlike the standard laser effect, this one targets a random player on the server. " +
                    "Use this endpoint to start the effect, and the associated restart endpoint to have it target a new player.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There's a problem with the request body, or an effect group with the given uuid exists and is already active"),
                    @ApiResponse(responseCode = "501", description = "The start point id in the request does not have a point ingame.")
            }
    )
    public static Response start(
            @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                    UUID id,
            @RequestBody(description = "Array of TargetedLaser models", required = true)
                    TargetedLaserModel[] models) {
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
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    public static Response restart(
            @Parameter(description = "UUID of the group that will be restarted", required = true)
                    UUID id) {
        try {
            if (!EffectManager.instanceOf(id, TargetedLaserEffect.class))
                return BAD_REQUEST;

            EffectManager.restartEffect(id);
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
