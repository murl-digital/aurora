package fyi.sorenneedscoffee.aurora.http.endpoints.dragon;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.dragon.DragonModel;
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

public class DragonEndpoint extends Endpoint {

    public DragonEndpoint() {
        this.path = Pattern.compile("/effects/dragon/.+/(start|restart)");
    }

    @Operation(
            summary = "the moment you've been waiting for!",
            description = "The infamous dying dragon effect. The death animation will play at the specified point and flash with increasing intensity.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There's a problem with the request body, or an effect group with the given uuid exists and is already active"),
                    @ApiResponse(responseCode = "501", description = "The start and/or point id in the request does not have a point ingame.")
            }
    )
    public static Response start(
            @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                    UUID id,
            @RequestBody(description = "Array of Dragon models", required = true)
                    DragonModel[] models) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

            EffectGroup group = new EffectGroup(id);
            for (DragonModel model : models) {
                Point point = Aurora.pointUtil.getPoint(model.pointId);
                if (point == null)
                    return POINT_DOESNT_EXIST;

                DragonEffect effect = new DragonEffect(point, model.isStatic);
                group.add(effect);
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Throwable e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Operation(
            summary = "that moment, again!",
            description = "Restarts the animation bound to the given UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There is either no group with the given UUID or it does not consist of Dragon Effects.")
            }
    )
    public static Response restart(@Parameter(description = "UUID that the group that will be restarted", required = true)
                                           UUID id) {
        try {
            if (EffectManager.instanceOf(id, DragonEffect.class))
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
                    DragonModel[] models = Aurora.gson.fromJson(bodyStream, DragonModel[].class);
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
