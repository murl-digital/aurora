package fyi.sorenneedscoffee.aurora.http.endpoints.time;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.time.TimeShiftEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.time.TimeShiftModel;
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

@Path("effects/time/{id}")
public class TimeShiftEndpoint extends Endpoint {

    @Path("/start")
    @Operation(
            summary = "Start the TimeShift effect",
            description = "The TimeShift effect affects the ingame daylight cycle, incrementing it by the given amount every game tick. You can provide an array of request objects which will apply them al at the same time",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The effect group started successfully"),
                    @ApiResponse(responseCode = "400", description = "An effect group with the given uuid exists and is already active"),
                    @ApiResponse(responseCode = "501", description = "This effect requires that point 0 is already defined, the endpoint will respond with 501 if a point 0 is not found.")
            }
    )
    @POST
    @Consumes("application/json")
    public Response startEffect(@PathParam("id")
                                @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                                        UUID id,
                                @RequestBody(description = "Array of TimeShift models", required = true)
                                        TimeShiftModel[] models) {
        if (EffectManager.exists(id))
            return BAD_REQUEST;

        try {
            Point point = Aurora.pointUtil.getPoint(0);
            if (point == null)
                return POINT_DOESNT_EXIST;

            EffectGroup group = new EffectGroup(id);
            for (TimeShiftModel model : models) {
                group.add(new TimeShiftEffect(point, model.amount));
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }
}
