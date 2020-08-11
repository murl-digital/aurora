package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.LaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.laser.LaserModel;
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

@Path("effects/laser/{id}")
public class LaserEndpoint extends Endpoint {

    @Path("/start")
    @Operation(
            summary = "pew pew",
            description = "This effect takes advantage of the guardian laser, which shoots between the defined start and end points.",
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
                                 @RequestBody(description = "Array of Laser models", required = true)
                                         LaserModel[] request) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

            EffectGroup group = new EffectGroup(id);
            for (LaserModel model : request) {
                Point start = Aurora.pointUtil.getPoint(model.startId);
                Point end = Aurora.pointUtil.getPoint(model.endId);
                if (start == null || end == null)
                    return POINT_DOESNT_EXIST;
                group.add(new LaserEffect(start, end));
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Path("/trigger")
    @Operation(
            summary = "more pew pew",
            description = "Changes an active laser's color. Good for rhythmic stuff"
    )
    @POST
    public static Response trigger(@PathParam("id")
                                   @Parameter(description = "UUID of active laser group that will be colorchanged", required = true)
                                           UUID id) {
        try {
            if(!EffectManager.instanceOf(id, LaserEffect.class))
                return BAD_REQUEST;

            EffectManager.hotTriggerEffect(id);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }
}
