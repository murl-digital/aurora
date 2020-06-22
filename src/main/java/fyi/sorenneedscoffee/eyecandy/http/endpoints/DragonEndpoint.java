package fyi.sorenneedscoffee.eyecandy.http.endpoints;

import fyi.sorenneedscoffee.eyecandy.EyeCandy;
import fyi.sorenneedscoffee.eyecandy.effects.EffectGroup;
import fyi.sorenneedscoffee.eyecandy.effects.dragon.DragonEffect;
import fyi.sorenneedscoffee.eyecandy.http.Endpoint;
import fyi.sorenneedscoffee.eyecandy.http.requests.DragonModel;
import fyi.sorenneedscoffee.eyecandy.util.EffectManager;
import fyi.sorenneedscoffee.eyecandy.util.Point;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.UUID;

public class DragonEndpoint extends Endpoint {

    @Path("/effects/{id}/start/dragon")
    @POST
    @Consumes("application/json")
    public Response process(@PathParam("id") UUID id, InputStream stream) {
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in)) {
                return Response.status(400).build();
            }

            DragonModel[] request = EyeCandy.gson.fromJson(in, DragonModel[].class);

            EffectGroup group = new EffectGroup(id);
            for (DragonModel model : request) {
                Point point = EyeCandy.pointUtil.getPoint(model.pointId);
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
    @Path("/effects/{id}/restart/dragon")
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
