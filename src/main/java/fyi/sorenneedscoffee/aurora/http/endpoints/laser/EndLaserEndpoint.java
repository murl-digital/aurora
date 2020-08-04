package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.EndLaserEffect;
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

@Path("effects/endlaser/{id}")
public class EndLaserEndpoint extends Endpoint {

    @Path("/start")
    @Operation(
            summary = "*ominous humming*",
            description = "This effect takes advantage of the ender crystal beam most prominently seen in the Ender Dragon bossfight. " +
                    "Please note that while the effect is active, the originating crystal will be visible but without the bedrock base.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "There's a problem with the request body, or an effect group with the given uuid exists and is already active"),
                    @ApiResponse(responseCode = "501", description = "The start and/or point id in the request does not have a point ingame.")
            }
    )
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id")
                          @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                                  UUID id,
                          @RequestBody(description = "Array of Laser models", required = true)
                                  LaserModel[] models) {
        if (EffectManager.exists(id))
            return BAD_REQUEST;

        try {
            EffectGroup group = new EffectGroup(id);

            for (LaserModel model : models) {
                Point start = Aurora.pointUtil.getPoint(model.startId);
                Point end = Aurora.pointUtil.getPoint(model.endId);
                if (start == null || end == null)
                    return POINT_DOESNT_EXIST;

                group.add(new EndLaserEffect(start.getLocation(), end.getLocation()));
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }
}
