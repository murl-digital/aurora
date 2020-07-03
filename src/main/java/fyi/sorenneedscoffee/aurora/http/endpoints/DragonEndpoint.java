package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.DragonModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

@Path("/effects/dragon/{id}")
public class DragonEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response process(@PathParam("id") UUID id, InputStream stream) {
        if (EffectManager.exists(id))
            return Response.status(400).build();
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in, DragonModel[].class)) {
                return Response.status(400).build();
            }

            DragonModel[] request = Aurora.gson.fromJson(in, DragonModel[].class);

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
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @POST
    @Path("/restart")
    public Response stop(@PathParam("id") UUID id) {
        if (!EffectManager.exists(id))
            return Response.status(404).build();

        try {
            EffectManager.restartEffect(id);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
}
