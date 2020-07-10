package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.laser.LaserEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.LaserModel;
import fyi.sorenneedscoffee.aurora.http.models.ParticleModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

@Path("/effects/laser/{id}")
public class LaserEndpoint extends Endpoint {

    @Path("/start")
    @POST
    public Response start(@PathParam("id") UUID id, InputStream stream) {
        if(EffectManager.exists(id))
            return Response.status(400).build();
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in, LaserModel[].class)) {
                return Response.status(400).build();
            }

            LaserModel[] request = Aurora.gson.fromJson(in, LaserModel[].class);

            EffectGroup group = new EffectGroup(id);
            for(LaserModel model : request) {
                Point start = Aurora.pointUtil.getPoint(model.startId);
                Point end = Aurora.pointUtil.getPoint(model.endId);
                if (start == null || end == null)
                    return Response.status(400).build();
                group.add(new LaserEffect(start, end));
            }

            EffectManager.startEffect(group);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}
