package fyi.sorenneedscoffee.aurora.http.endpoints.bossbar;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.Response;
import fyi.sorenneedscoffee.aurora.util.BarManager;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class BossBarClearEndpoint extends Endpoint {

    public BossBarClearEndpoint() {
        this.path = Pattern.compile("/bar/clear");
    }

    public static Response clear() {
        try {
            if (!BarManager.isActive())
                return OK;

            BarManager.clearBars();
            return OK;
        } catch (Exception e) {
            Aurora.logger.warning(e.getMessage());
            Aurora.logger.warning(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR.clone().entity(e.getMessage() + "\n\n" + ExceptionUtils.getStackTrace(e)).build();
        }
    }

    @Override
    public Response handle(String[] tokens, InputStreamReader bodyStream) {
        return clear();
    }
}
