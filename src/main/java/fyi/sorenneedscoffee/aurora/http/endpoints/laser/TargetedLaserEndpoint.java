package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.TargetedLaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.laser.TargetedLaserModel;
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

@Path("/effects/targetedlaser/{id}")
public class TargetedLaserEndpoint extends Endpoint {

    @Path("/start")
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
    @POST
    @Consumes("application/json")
    public static Response start(@PathParam("id")
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

    @Path("/restart")
    @POST
    public static Response restart(@PathParam("id")
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
}
