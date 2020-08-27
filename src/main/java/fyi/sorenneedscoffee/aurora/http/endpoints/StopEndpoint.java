package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang.exception.ExceptionUtils;

import fyi.sorenneedscoffee.aurora.http.Response;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class StopEndpoint extends Endpoint {

    public StopEndpoint() {
        this.path = Pattern.compile("/effects/(all|.+)/stop");
    }

    @Operation(
            summary = "Stop effect",
            description = "Stops an effect group with the given UUID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Effect stopped successfully "),
                    @ApiResponse(responseCode = "404", description = "There is no active effect group with the given UUID")
            }
    )
    public static Response stop(
            @Parameter(description = "UUID of the effect group that will be stopped", required = true)
                    UUID id) {
        try {
            if (!EffectManager.exists(id))
                return NOT_FOUND;

            EffectManager.stopEffect(id);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Operation(
            summary = "Stop all effects",
            description = "Stops all active effect groups",
            responses = {
                    @ApiResponse(responseCode = "200", description = "All effects stopped successfully")
            }
    )
    public static Response stopAll() {
        try {
            EffectManager.stopAll(false);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader ignore) {
        if ("all".equals(tokens[1]))
            return stopAll();

        try {
            UUID id = UUID.fromString(tokens[1]);
            return stop(id);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
