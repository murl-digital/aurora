package fyi.sorenneedscoffee.aurora.http.endpoints.stop;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class StopAllEndpoint extends Endpoint {

    public StopAllEndpoint() {
        this.path = Pattern.compile("/effects/all/stop");
    }

    private Response stopAll() {
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
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        return stopAll();
    }
}
