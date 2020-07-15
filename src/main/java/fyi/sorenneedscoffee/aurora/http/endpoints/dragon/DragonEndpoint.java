package fyi.sorenneedscoffee.aurora.http.endpoints.dragon;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.dragon.DragonModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

@Path("/effects/dragon/{id}")
public class DragonEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response process(@PathParam("id") UUID id, DragonModel[] request) {
        if (EffectManager.exists(id))
            return Response.status(400).build();

        try {
            EffectGroup group = new EffectGroup(id);
            for (DragonModel model : request) {
                Point point = Aurora.pointUtil.getPoint(model.pointId);
                if (point == null) {
                    return Response.status(400).build();
                }
                DragonEffect effect = new DragonEffect(point, model.isStatic);
                group.add(effect);
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }

    @POST
    @Path("/restart")
    public Response stop(@PathParam("id") UUID id) {
        if (!EffectManager.exists(id))
            return NOT_FOUND;

        try {
            EffectManager.restartEffect(id);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }
}
