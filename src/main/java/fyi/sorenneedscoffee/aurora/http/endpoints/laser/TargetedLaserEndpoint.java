package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.TargetedLaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.laser.TargetedLaserModel;
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

@Path("/effects/targetedlaser/{id}")
public class TargetedLaserEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, TargetedLaserModel[] request) {
        if (EffectManager.exists(id))
            return BAD_REQUEST;

        try {
            EffectGroup group = new EffectGroup(id);
            for (TargetedLaserModel model : request) {
                Point start = Aurora.pointUtil.getPoint(model.startId);
                if (start == null)
                    return Response.status(400).build();
                group.add(new TargetedLaserEffect(start));
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Exception e) {
            Aurora.logger.severe(e.getMessage());
            Aurora.logger.severe(ExceptionUtils.getStackTrace(e));
            return SERVER_ERROR;
        }
    }

    @Path("/restart")
    @POST
    public Response restart(@PathParam("id") UUID id) {
        if (!EffectManager.exists(id))
            return BAD_REQUEST;

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
