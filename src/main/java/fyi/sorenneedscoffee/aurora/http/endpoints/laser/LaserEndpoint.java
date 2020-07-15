package fyi.sorenneedscoffee.aurora.http.endpoints.laser;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.LaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.laser.LaserModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

@Path("/effects/laser/{id}")
public class LaserEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, LaserModel[] request) {
        if (EffectManager.exists(id))
            return BAD_REQUEST;

        try {
            EffectGroup group = new EffectGroup(id);
            for (LaserModel model : request) {
                Point start = Aurora.pointUtil.getPoint(model.startId);
                Point end = Aurora.pointUtil.getPoint(model.endId);
                if (start == null || end == null)
                    return BAD_REQUEST;
                group.add(new LaserEffect(start, end));
            }

            EffectManager.startEffect(group);
            return OK;
        } catch (Exception e) {
            return SERVER_ERROR;
        }
    }
}
