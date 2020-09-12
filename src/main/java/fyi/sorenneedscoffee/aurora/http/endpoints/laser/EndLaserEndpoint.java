package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.EndLaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.http.models.laser.LaserModel;
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

public class EndLaserEndpoint extends Endpoint {

    public EndLaserEndpoint() {
        this.path = Pattern.compile("/effects/endlaser/.+/start");
    }

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
    public static Response start(
            @Parameter(description = "UUID that will be assigned to the effect group", required = true)
                    UUID id,
            @RequestBody(description = "Array of Laser models", required = true)
                    LaserModel[] models) {
        try {
            if (EffectManager.exists(id))
                return BAD_REQUEST;

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
        } catch (Throwable e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        try {
            UUID id = UUID.fromString(tokens[2]);
            LaserModel[] models = Aurora.gson.fromJson(bodyStream, LaserModel[].class);
            return start(id, models);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
