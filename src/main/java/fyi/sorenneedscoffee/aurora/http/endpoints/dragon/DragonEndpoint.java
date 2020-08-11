package fyi.sorenneedscoffee.aurora.http.endpoints.dragon;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.dragon.DragonModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("effects/dragon/{id}")
public class DragonEndpoint extends Endpoint {

    @Path("/start")
    @Operation(
            summary = "the moment you've been waiting for!",
            description = "The infamous dying dragon effect. The death animation will play at the specified point and flash with increasing intensity.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There's a problem with the request body, or an effect group with the given uuid exists and is already active"),
                    @ApiResponse(responseCode = "501", description = "The start and/or point id in the request does not have a point ingame.")
            }
    )
    @POST
    @Consumes("application/json")
    public static Response start(@PathParam("id")
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
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Path("/restart")
    @Operation(
            summary = "that moment, again!",
            description = "Restarts the animation bound to the given UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There is either no group with the given UUID or it does not consist of Dragon Effects.")
            }
    )
    @POST
    public static Response restart(@PathParam("id")
                                   @Parameter(description = "UUID that the group that will be restarted", required = true)
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
}
