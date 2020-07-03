package fyi.sorenneedscoffee.aurora.http.endpoints;

import fyi.sorenneedscoffee.aurora.Aurora;
import fyi.sorenneedscoffee.aurora.effects.EffectGroup;
import fyi.sorenneedscoffee.aurora.effects.particle.ParticleEffect;
import fyi.sorenneedscoffee.aurora.effects.particle.Region;
import fyi.sorenneedscoffee.aurora.http.Endpoint;
import fyi.sorenneedscoffee.aurora.http.models.ParticleModel;
import fyi.sorenneedscoffee.aurora.util.EffectManager;
import fyi.sorenneedscoffee.aurora.util.Point;
import org.bukkit.Particle;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("effects/particle/{id}")
public class ParticleEndpoint extends Endpoint {

    @Path("/start")
    @POST
    @Consumes("application/json")
    public Response start(@PathParam("id") UUID id, InputStream stream) {
        if (EffectManager.exists(id))
            return Response.status(400).build();
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in, ParticleModel[].class)) {
                return Response.status(400).build();
            }

            EffectGroup group;
            try {
                group = constructGroup(id, in, false);
            } catch (IllegalArgumentException e) {
                return Response.status(422).build();
            }

            EffectManager.startEffect(group);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @Path("/trigger")
    @POST
    @Consumes("application/json")
    public Response trigger(@PathParam("id") UUID id, InputStream stream) {
        try {
            byte[] target = new byte[stream.available()];
            stream.read(target);
            String in = new String(target);

            if (isInvalid(in, ParticleModel[].class)) {
                return Response.status(400).build();
            }

            EffectGroup group;
            try {
                group = constructGroup(id, in, true);
            } catch (IllegalArgumentException e) {
                return Response.status(422).build();
            } catch (NullPointerException e) {
                return Response.status(400).build();
            }

            EffectManager.triggerEffect(group);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    private EffectGroup constructGroup(UUID id, String jsonString, boolean ignoreRandomized) throws IllegalArgumentException, NullPointerException {
        ParticleModel[] request = Aurora.gson.fromJson(jsonString, ParticleModel[].class);
        EffectGroup group = new EffectGroup(id);
        for (ParticleModel model : request) {
            List<Point> points = new ArrayList<>();
            for (int i : model.region.pointIds) {
                Point point = Aurora.pointUtil.getPoint(i);
                if (point == null)
                    throw new NullPointerException();
                points.add(Aurora.pointUtil.getPoint(i));
            }
            Region region = new Region(
                    points.toArray(new Point[0]),
                    model.region.type,
                    model.region.density,
                    !ignoreRandomized && model.region.randomized,
                    model.region.equation
            );
            Particle particle;
            particle = Particle.valueOf(model.name);
            ParticleEffect effect = new ParticleEffect(region, particle);
            group.add(effect);
        }

        return group;
    }
}
