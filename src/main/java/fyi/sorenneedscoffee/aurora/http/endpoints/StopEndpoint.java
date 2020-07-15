package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("effects")
public class StopEndpoint extends Endpoint {

    @Path("/{id}/stop")
    @POST
    public Response stop(@PathParam("id") UUID id) {
        if (!EffectManager.exists(id))
            return Response.status(404).build();

        try {
            EffectManager.stopEffect(id);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }

    @Path("/all/stop")
    @POST
    public Response stopAll() {
        try {
            EffectManager.stopAll();
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }
}
