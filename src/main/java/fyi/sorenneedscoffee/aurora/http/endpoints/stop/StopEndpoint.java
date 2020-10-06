package fyi.sorenneedscoffee.aurora.http.endpoints.stop;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

public class StopEndpoint extends Endpoint {

    public StopEndpoint() {
        this.path = Pattern.compile("/effects/.+/stop");
    }

    private Response stop(UUID id) {
        try {
            if (!EffectManager.exists(id))
                return NOT_FOUND;

            EffectManager.stopEffect(id);
            return OK;
        } catch (Exception e) {
            if (e.getMessage() != null) Aurora.logger.warning(e.getMessage());
            Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
            String message = e.getMessage() != null ? e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e) : ExceptionUtils.getStackTrace(e);
            return SERVER_ERROR.clone().entity(message).build();
        }
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader ignore) {
        try {
            UUID id = UUID.fromString(tokens[1]);
            return stop(id);
        } catch (IllegalArgumentException e) {
            return BAD_REQUEST;
        }
    }
}
