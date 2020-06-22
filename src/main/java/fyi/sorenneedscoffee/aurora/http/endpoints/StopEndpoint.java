package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.util.EffectManager;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("effects/{id}/stop")
public class StopEndpoint {

    @POST
    public Response stop(@PathParam("id") UUID id) {
        if (!EffectManager.exists(id))
            return Response.status(404).build();

        try {
            EffectManager.stopEffect(id);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
}
